package com.mohit.fanout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {

    @Test
    void csvParserParses() throws Exception {
        RecordParser p = new CsvRecordParser();
        InputRecord r = p.parse("10,Test,1.23,1700000000000");
        assertEquals("10", r.id());
        assertEquals("Test", r.name());
        assertEquals(1.23, r.amount(), 0.0001);
    }
}
