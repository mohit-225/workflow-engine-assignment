package com.mohit.fanout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        String path = (args != null && args.length >= 1) ? args[0] : "src/main/resources/sample.csv";
        String fmt = (args != null && args.length >= 2) ? args[1] : "csv";

        InputFormat format = InputFormat.from(fmt);
        log.info("Starting Fan-Out Engine | input={} | format={}", path, format);

        EngineConfig config = EngineConfig.defaultConfig();

        RecordParser parser = switch (format) {
            case CSV -> new CsvRecordParser();
            case JSONL -> new JsonlRecordParser();
            case FIXED_WIDTH -> new FixedWidthRecordParser();
        };

        // Build sink pipelines (transform strategy + sink client)
        SinkPipeline rest = SinkPipeline.restPipeline();
        SinkPipeline grpc = SinkPipeline.grpcPipeline();
        SinkPipeline mq   = SinkPipeline.queuePipeline();
        SinkPipeline wide = SinkPipeline.wideColumnPipeline();

        FanOutEngine engine = new FanOutEngine(config, parser, rest, grpc, mq, wide);
        engine.run(path);

        log.info("Done.");
    }
}
