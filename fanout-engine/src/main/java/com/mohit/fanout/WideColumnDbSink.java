package com.mohit.fanout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/** Mock Wide-column DB sink (simulated batch write). */
public class WideColumnDbSink implements SinkClient<Map<String, Object>> {
    private static final Logger log = LoggerFactory.getLogger(WideColumnDbSink.class);

    @Override
    public String name() { return "WIDE_COLUMN_DB"; }

    @Override
    public void sendBatch(List<Map<String, Object>> payloads) throws Exception {
        Thread.sleep(6L);
        if (ThreadLocalRandom.current().nextInt(2200) == 0) {
            throw new TransientSinkException("Wide-column transient error");
        }
        if (payloads.size() > 0 && ThreadLocalRandom.current().nextInt(8000) == 0) {
            log.info("[WideDB] sample payload={}", payloads.get(0));
        }
    }
}
