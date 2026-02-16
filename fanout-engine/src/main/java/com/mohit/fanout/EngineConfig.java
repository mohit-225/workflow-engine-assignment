package com.mohit.fanout;

public record EngineConfig(
        int workerThreads,
        int queueCapacity,
        int batchSize,
        int maxRetries
) {
    public static EngineConfig defaultConfig() {
        return new EngineConfig(
                8,      // worker threads
                5000,   // bounded queue capacity for backpressure
                100,    // batching to sinks
                2       // retries for transient sink failures
        );
    }
}
