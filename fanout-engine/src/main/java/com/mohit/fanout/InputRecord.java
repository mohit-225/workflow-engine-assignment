package com.mohit.fanout;

import java.util.Map;

/** A normalized record independent of input format. */
public record InputRecord(String id, String name, double amount, long eventTimeEpochMs, Map<String, String> extras) {}
