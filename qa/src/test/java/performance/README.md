# Performance Testing with k6

This documentation explains how to run performance tests for the `fx-market-data` service using **k6**.
These tests include peak load and stress testing, to check system performance.

---
## **How to Run Tests**

Gradle task 'connectorRun'
- triggers `connectorPerfTest.js`: Simulates peak load conditions on /emitEvent to measure system performance.
Gradle task 'processorRun'
- triggers `processorPerfTest.js`: measures FX rate update latency by sending data on /emitEvent, polling for updates from /fx/rates/{pair}, verifying consistency, and aggregating response time statistics.
---


### See the report

Now it's generated as an artifact after performancelocal - Run task.
qa/build/test-results

