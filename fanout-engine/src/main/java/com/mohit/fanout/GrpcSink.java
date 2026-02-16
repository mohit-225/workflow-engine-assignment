package com.mohit.fanout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Mock gRPC sink (simulated unary/streaming). */
public class GrpcSink implements SinkClient<String> {
    private static final Logger log = LoggerFactory.getLogger(GrpcSink.class);

    @Override
    public String name() { return "GRPC"; }

    @Override
    public void sendBatch(List<String> payloads) throws Exception {
        Thread.sleep(4L);
        if (ThreadLocalRandom.current().nextInt(2500) == 0) {
            throw new TransientSinkException("gRPC transient error");
        }
        if (payloads.size() > 0 && ThreadLocalRandom.current().nextInt(6000) == 0) {
            log.info("[gRPC] sample payload={}", payloads.get(0));
        }
    }
}
