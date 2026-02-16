package com.mohit.fanout;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * JSONL: one JSON object per line.
 * Required fields: id, name, amount, eventTimeEpochMs
 */
public class JsonlRecordParser implements RecordParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public InputRecord parse(String line) throws Exception {
        JsonNode n = MAPPER.readTree(line);
        String id = n.get("id").asText();
        String name = n.get("name").asText();
        double amount = n.get("amount").asDouble();
        long ts = n.get("eventTimeEpochMs").asLong();

        Map<String, String> extras = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> it = n.fields();
        while (it.hasNext()) {
            var e = it.next();
            String k = e.getKey();
            if (!k.equals("id") && !k.equals("name") && !k.equals("amount") && !k.equals("eventTimeEpochMs")) {
                extras.put(k, e.getValue().asText());
            }
        }
        return new InputRecord(id, name, amount, ts, extras);
    }
}
