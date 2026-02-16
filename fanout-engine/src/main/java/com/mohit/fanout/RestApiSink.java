package com.mohit.fanout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Mock REST API sink (simulated HTTP/2 POST). */
public class RestApiSink implements SinkClient<String> {
    private static final Logger log = LoggerFactory.getLogger(RestApiSink.class);

    @Override
    public String name() { return "REST_API"; }

    @Override
    public void sendBatch(List<String> payloads) throws Exception {
        // Simulate latency + occasional transient failure
        Thread.sleep(5L);
        if (ThreadLocalRandom.current().nextInt(2000) == 0) {
            throw new TransientSinkException("REST transient error");
        }
        if (payloads.size() > 0 && ThreadLocalRandom.current().nextInt(5000) == 0) {
            log.info("[REST] sample payload={}", payloads.get(0));
        }
    }
}
