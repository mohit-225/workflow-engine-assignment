# High-Throughput Fan-Out Engine (Assignment 2)

## Overview
This project implements a **streaming ingestion + transformation + fan-out** engine in **Java 17**.
It reads very large files (e.g., 100GB CSV/JSONL/fixed-width) without loading everything into memory,
transforms each record into sink-specific payloads using the **Strategy Pattern**, and distributes
to multiple **mock sinks** (REST, gRPC, Message Queue, Wide-Column DB).

Key focus:
- Streaming ingestion (constant memory)
- High throughput fan-out
- Backpressure & bounded buffering (to avoid overloading sinks)
- Extensible transformations (Strategy Pattern)

## Architecture
FileIngestion (stream reader)

   |
   
   v
   
RecordParser (CSV / JSONL / FixedWidth)
   
   |
   
   v
   
FanOutEngine

   |
   
   +--> Transformer (Strategy Pattern) per sink
   
   |
   
   +--> SinkClient (mock)
   
          RestApiSink (HTTP/2 simulated)
          
          GrpcSink (stream/unary simulated)
          
          MessageQueueSink (producer simulated)
          
          WideColumnDbSink (batch write simulated)

## How Backpressure Works
- The engine uses a **bounded queue** between ingestion and distribution.
- If sinks are slow, the queue fills up; ingestion blocks and does not consume more input.
- This prevents OOM and prevents overwhelming downstream systems.

## Run
### Build
```bash
mvn clean package
```

### Run via Maven (recommended)
```bash
mvn exec:java -Dexec.mainClass="com.mohit.fanout.App"
```

### Run via runnable shaded jar
```bash
java -jar target/fanout-engine-1.0.0-shaded.jar
```

## Demo Input
A sample CSV is included at:
`src/main/resources/sample.csv`

You can run with a custom file path:
```bash
mvn exec:java -Dexec.mainClass="com.mohit.fanout.App" -Dexec.args="src/main/resources/sample.csv csv"
```
Second arg is format: `csv | jsonl | fixed`

## Configuration (defaults in code)
- Batch size: 100
- Worker threads: 8
- Queue capacity: 5_000
- Per-sink rate limiting simulated (sleep + random failure)
- Retries: 2

## Notes for Evaluators
- **Ingestion** is streaming and does not load the entire file.
- **Transformations** use Strategy Pattern; adding a new sink is a new strategy + sink client.
- **Sinks** are mocked but simulate latency, batching, and occasional transient failures.
- **Backpressure** is implemented using bounded queues and blocking producer behavior.

## Non-Functional Characteristics
- Performance: Optimized for throughput and constant memory usage
- Reliability: Crash-safe via persistence
- Scalability: Extensible architecture
- Maintainability: Clean separation of concerns
- Extensibility: Strategy pattern for pluggable sinks
- Backpressure handling (Assignment 2)


## Future Improvements
- Real HTTP/2 client + real gRPC stubs
- Metrics (throughput, lag, retry counts)
- Config via YAML/CLI flags
- Exactly-once delivery with offsets/checkpoints
- Adaptive concurrency based on sink latency
