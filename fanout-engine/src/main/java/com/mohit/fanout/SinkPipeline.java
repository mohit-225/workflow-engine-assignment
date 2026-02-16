package com.mohit.fanout;

import java.util.Map;

/**
 * Represents one sink: transformation strategy + sink client.
 * This keeps Strategy Pattern explicit and easy to extend.
 */
public class SinkPipeline<T> {
    private final String id;
    private final TransformationStrategy<T> transformer;
    private final SinkClient<T> sink;

    public SinkPipeline(String id, TransformationStrategy<T> transformer, SinkClient<T> sink) {
        this.id = id;
        this.transformer = transformer;
        this.sink = sink;
    }

    public String id() { return id; }
    public TransformationStrategy<T> transformer() { return transformer; }
    public SinkClient<T> sink() { return sink; }

    public static SinkPipeline<String> restPipeline() {
        return new SinkPipeline<>("rest", new RestJsonStrategy(), new RestApiSink());
    }

    public static SinkPipeline<String> grpcPipeline() {
        return new SinkPipeline<>("grpc", new GrpcProtoStrategy(), new GrpcSink());
    }

    public static SinkPipeline<String> queuePipeline() {
        return new SinkPipeline<>("mq", new QueueXmlStrategy(), new MessageQueueSink());
    }

    public static SinkPipeline<Map<String, Object>> wideColumnPipeline() {
        return new SinkPipeline<>("wide", new WideColumnMapStrategy(), new WideColumnDbSink());
    }
}
