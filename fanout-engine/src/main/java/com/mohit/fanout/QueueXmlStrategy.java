package com.mohit.fanout;

/** Produces XML payload for Message Queue sink. */
public class QueueXmlStrategy implements TransformationStrategy<String> {
    @Override
    public String transform(InputRecord r) {
        return "<event><id>%s</id><name>%s</name><amount>%.2f</amount><ts>%d</ts></event>"
                .formatted(escape(r.id()), escape(r.name()), r.amount(), r.eventTimeEpochMs());
    }

    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
