package com.mohit.fanout;

public enum InputFormat {
    CSV, JSONL, FIXED_WIDTH;

    public static InputFormat from(String s) {
        if (s == null) return CSV;
        return switch (s.toLowerCase()) {
            case "csv" -> CSV;
            case "jsonl" -> JSONL;
            case "fixed", "fixedwidth", "fixed_width" -> FIXED_WIDTH;
            default -> CSV;
        };
    }
}
