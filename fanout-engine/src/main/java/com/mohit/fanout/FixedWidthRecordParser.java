package com.mohit.fanout;

import java.util.HashMap;
import java.util.Map;

/**
 * Fixed-width format (demo):
 * [0-9]   id (10 chars, right padded)
 * [10-39] name (30 chars, right padded)
 * [40-52] amount (13 chars, numeric)
 * [53-66] eventTimeEpochMs (14 chars, numeric)
 */
public class FixedWidthRecordParser implements RecordParser {

    @Override
    public InputRecord parse(String line) {
        if (line.length() < 67) {
            throw new IllegalArgumentException("Invalid fixed-width line: " + line);
        }
        String id = line.substring(0, 10).trim();
        String name = line.substring(10, 40).trim();
        double amount = Double.parseDouble(line.substring(40, 53).trim());
        long ts = Long.parseLong(line.substring(53, 67).trim());
        Map<String, String> extras = new HashMap<>();
        return new InputRecord(id, name, amount, ts, extras);
    }
}
