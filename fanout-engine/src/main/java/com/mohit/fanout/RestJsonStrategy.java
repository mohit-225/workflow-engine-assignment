package com.mohit.fanout;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;

/** Produces JSON payload for REST API sink. */
public class RestJsonStrategy implements TransformationStrategy<String> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String transform(InputRecord record) throws Exception {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", record.id());
        m.put("name", record.name());
        m.put("amount", record.amount());
        m.put("eventTime", record.eventTimeEpochMs());
        m.put("extras", record.extras());
        return MAPPER.writeValueAsString(m);
    }
}
