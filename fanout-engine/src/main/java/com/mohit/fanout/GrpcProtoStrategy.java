package com.mohit.fanout;

/**
 * Simulates Protobuf for gRPC. (No actual proto compiled, but shape is protobuf-like.)
 * Payload is a compact string representation for demo.
 */
public class GrpcProtoStrategy implements TransformationStrategy<String> {
    @Override
    public String transform(InputRecord record) {
        return "ProtoMsg{id=%s,name=%s,amount=%.2f,ts=%d}".formatted(
                record.id(), record.name(), record.amount(), record.eventTimeEpochMs()
        );
    }
}
