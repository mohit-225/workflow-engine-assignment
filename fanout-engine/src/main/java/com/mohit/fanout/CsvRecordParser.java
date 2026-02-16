package com.mohit.fanout;

import java.util.HashMap;
import java.util.Map;

/**
 * CSV format: id,name,amount,eventTimeEpochMs
 * Example: 1,Alice,12.34,1700000000000
 */
public class CsvRecordParser implements RecordParser {

    @Override
    public InputRecord parse(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid CSV line: " + line);
        }
        String id = parts[0].trim();
        String name = parts[1].trim();
        double amount = Double.parseDouble(parts[2].trim());
        long ts = Long.parseLong(parts[3].trim());
        Map<String, String> extras = new HashMap<>();
        return new InputRecord(id, name, amount, ts, extras);
    }
}
