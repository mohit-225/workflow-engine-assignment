package com.mohit.fanout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Streaming fan-out engine with backpressure.
 *
 * - Ingestion reads lines and parses into InputRecord.
 * - Parsed records go into a bounded queue (backpressure).
 * - Worker threads pull records, transform for each sink (Strategy Pattern), batch, and flush to sinks.
 */
public class FanOutEngine {
    private static final Logger log = LoggerFactory.getLogger(FanOutEngine.class);

    private final EngineConfig cfg;
    private final RecordParser parser;
    private final SinkPipeline<?>[] pipelines;

    public FanOutEngine(EngineConfig cfg, RecordParser parser, SinkPipeline<?>... pipelines) {
        this.cfg = cfg;
        this.parser = parser;
        this.pipelines = pipelines;
    }

    public void run(String filePath) throws Exception {
        if (!Files.exists(Path.of(filePath))) {
            throw new IllegalArgumentException("Input file not found: " + filePath);
        }

        BlockingQueue<InputRecord> queue = new ArrayBlockingQueue<>(cfg.queueCapacity());
        ExecutorService workers = Executors.newFixedThreadPool(cfg.workerThreads());
        CountDownLatch doneSignal = new CountDownLatch(cfg.workerThreads());
        AtomicBoolean stop = new AtomicBoolean(false);

        Instant start = Instant.now();

        // Start workers
        for (int i = 0; i < cfg.workerThreads(); i++) {
            workers.submit(() -> {
                try {
                    workerLoop(queue, stop);
                } finally {
                    doneSignal.countDown();
                }
            });
        }

        // Ingestion (single-threaded streaming)
        long lines = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath), 1024 * 1024)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // allow comments
                InputRecord record = parser.parse(line);
                queue.put(record); // blocks when queue is full (backpressure)
                lines++;
                if (lines % 1_000_000 == 0) {
                    log.info("Ingested {} records...", lines);
                }
            }
        }

        // Signal stop: put poison pills
        stop.set(true);
        for (int i = 0; i < cfg.workerThreads(); i++) {
            queue.put(POISON_PILL);
        }

        doneSignal.await(10, TimeUnit.MINUTES);
        workers.shutdownNow();

        Duration d = Duration.between(start, Instant.now());
        double sec = Math.max(1.0, d.toMillis() / 1000.0);
        log.info("Processed {} records in {} ms (~{}/sec)", lines, d.toMillis(), (long)(lines / sec));
    }

    private static final InputRecord POISON_PILL = new InputRecord("__POISON__", "", 0.0, 0L, java.util.Map.of());

    private void workerLoop(BlockingQueue<InputRecord> queue, AtomicBoolean stop) {
        // Per-pipeline batching buffers (thread-local)
        var buffers = new java.util.HashMap<String, List<Object>>();
        for (SinkPipeline<?> p : pipelines) {
            buffers.put(p.id(), new ArrayList<>(cfg.batchSize()));
        }

        try {
            while (true) {
                InputRecord r = queue.take();
                if (r == POISON_PILL) {
                    flushAll(buffers);
                    return;
                }

                for (SinkPipeline<?> p : pipelines) {
                    Object payload = transform(p, r);
                    List<Object> buf = buffers.get(p.id());
                    buf.add(payload);
                    if (buf.size() >= cfg.batchSize()) {
                        flushOne(p, buf);
                    }
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            flushAll(buffers);
        } catch (Exception e) {
            log.error("Worker fatal error", e);
            flushAll(buffers);
        }
    }

    private Object transform(SinkPipeline<?> p, InputRecord r) throws Exception {
        @SuppressWarnings("unchecked")
        TransformationStrategy<Object> t = (TransformationStrategy<Object>) p.transformer();
        return t.transform(r);
    }

    private void flushAll(java.util.Map<String, List<Object>> buffers) {
        for (SinkPipeline<?> p : pipelines) {
            List<Object> buf = buffers.get(p.id());
            if (buf != null && !buf.isEmpty()) {
                flushOne(p, buf);
            }
        }
    }

    private void flushOne(SinkPipeline<?> p, List<Object> buf) {
        @SuppressWarnings("unchecked")
        SinkClient<Object> sink = (SinkClient<Object>) p.sink();

        int attempts = 0;
        while (true) {
            try {
                sink.sendBatch(List.copyOf(buf));
                buf.clear();
                return;
            } catch (TransientSinkException te) {
                attempts++;
                if (attempts > cfg.maxRetries()) {
                    log.warn("Dropping batch after {} retries | sink={} | reason={}", cfg.maxRetries(), sink.name(), te.getMessage());
                    buf.clear();
                    return;
                }
                backoff(attempts);
            } catch (Exception e) {
                log.error("Non-retryable sink failure | sink={}", sink.name(), e);
                buf.clear();
                return;
            }
        }
    }

    private void backoff(int attempts) {
        try {
            Thread.sleep(10L * attempts);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
