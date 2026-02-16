# Workflow Engine Assignments

This repository contains two assignments:

## Assignment 1 – Durable Execution Engine
A crash-safe deterministic workflow engine using persistent H2 storage.

Features:
- Deterministic replay
- Idempotent step execution
- Crash recovery
- Logical sequence clock

Run:
mvn exec:java -Dexec.mainClass="com.mohit.durable.App"

---

## Assignment 2 – High Throughput Fan-Out Engine
A streaming ingestion and multi-sink fan-out engine with backpressure.

Features:
- Streaming file ingestion (constant memory)
- Strategy Pattern transformations
- Parallel processing
- Backpressure using bounded queue
- Multi-sink distribution (REST, gRPC, MQ, Wide DB)

Run:
mvn exec:java -Dexec.mainClass="com.mohit.fanout.App"
