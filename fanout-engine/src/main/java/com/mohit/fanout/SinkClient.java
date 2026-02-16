package com.mohit.fanout;

import java.util.List;

/** A sink receives payloads in batches. */
public interface SinkClient<T> {
    String name();
    void sendBatch(List<T> payloads) throws Exception;
}
