package com.mohit.fanout;

/** Strategy Pattern: transform normalized record into sink-specific payload. */
public interface TransformationStrategy<T> {
    T transform(InputRecord record) throws Exception;
}
