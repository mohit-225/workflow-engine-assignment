package com.mohit.fanout;

import java.util.LinkedHashMap;
import java.util.Map;

/** Produces an Avro/CQL-map like structure for wide-column DB sink. */
public class WideColumnMapStrategy implements TransformationStrategy<Map<String, Object>> {
    @Override
    public Map<String, Object> transform(InputRecord r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("pk", r.id());
        m.put("name", r.name());
        m.put("amount", r.amount());
        m.put("eventTimeEpochMs", r.eventTimeEpochMs());
        m.putAll(r.extras());
        return m;
    }
}
