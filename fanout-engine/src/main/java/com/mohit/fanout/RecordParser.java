package com.mohit.fanout;

/**
 * Parses one line into a normalized InputRecord.
 * Implementations: CSV, JSONL, Fixed-width.
 */
public interface RecordParser {
    InputRecord parse(String line) throws Exception;
}
