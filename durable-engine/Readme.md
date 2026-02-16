# Durable Execution Engine (Assignment 1)

## Executive Summary

This project implements a simplified **Durable Execution Engine**
inspired by workflow orchestration systems such as Temporal and AWS Step
Functions.

The engine guarantees: - Deterministic execution - Crash recovery -
Idempotent step handling - Persistent workflow state using an embedded
H2 database

------------------------------------------------------------------------

## Problem Statement

Design an execution engine that: - Executes workflow steps
sequentially - Persists step results - Recovers from crashes - Resumes
execution from the last incomplete step - Ensures already completed
steps are not re-executed

------------------------------------------------------------------------

## Architecture

┌──────────────────────────────┐

│ App │

│ (Workflow Definition) │

└───────────────┬──────────────┘

│
▼

┌──────────────────────────────┐

│ DurableExecutor │

│ (Core Engine) │

│ • Logical Clock │

│ • Step Execution │

│ • Replay Detection │

└───────────────┬──────────────┘

│
▼

┌──────────────────────────────┐

│ StepStore │

│ (Persistence Layer) │

│ • Save Step Results │

│ • Check Step Exists │

│ • Retrieve Stored Result │

└───────────────┬──────────────┘

│
▼

┌──────────────────────────────┐

│ H2 Embedded DB │

│ (Persistent State) │

└──────────────────────────────┘


------------------------------------------------------------------------

## Core Components

### 1. App.java

Defines workflow steps and simulates crash behavior.

### 2. DurableExecutor.java

-   Maintains logical sequence number
-   Determines execute vs replay
-   Persists results before moving forward

### 3. StepStore.java

-   Saves step results
-   Checks if step exists
-   Retrieves stored result

### 4. DatabaseManager.java

-   Initializes database
-   Manages connections
-   Creates schema if not exists

------------------------------------------------------------------------

## Data Model

Table: steps

workflow_id (VARCHAR) step_name (VARCHAR) sequence_number (INT) result
(CLOB)

Primary Key: (workflow_id, sequence_number)

The sequence_number acts as a logical clock ensuring deterministic
ordering.

------------------------------------------------------------------------

## Execution Flow

### First Run

Executing step-1\
Executing step-2\
Simulating crash...

### Second Run

Replaying step-1\
Replaying step-2\
Executing step-3\
Workflow completed.

------------------------------------------------------------------------

## How To Run

### Build

mvn clean package

### Run

mvn exec:java -Dexec.mainClass="com.mohit.durable.App"

### Reset State

Delete: durable_db.mv.db\
durable_db.trace.db

------------------------------------------------------------------------

## Non-Functional Considerations

-   Deterministic replay using logical clock
-   Crash safety via persistence before progression
-   Lightweight embedded database
-   Clean separation of concerns
-   Minimal external dependencies

------------------------------------------------------------------------

## Non-Functional Characteristics

- Performance: Optimized for throughput and constant memory usage
- Reliability: Crash-safe via persistence
- Scalability: Extensible architecture
- Maintainability: Clean separation of concerns
- Extensibility: Strategy pattern for pluggable sinks
- Backpressure handling (Assignment 2)

------------------------------------------------------------------------

## Future Improvements

-   Transactional boundaries
-   Retry policies with exponential backoff
-   Logging framework integration
-   REST API layer
-   Parallel step orchestration

------------------------------------------------------------------------

## Author

Mohit
