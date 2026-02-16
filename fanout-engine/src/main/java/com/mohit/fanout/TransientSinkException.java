package com.mohit.fanout;

/** Simulated transient failure (retryable). */
public class TransientSinkException extends RuntimeException {
    public TransientSinkException(String message) { super(message); }
}
