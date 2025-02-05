# Performance Testing with k6

This documentation explains how to run performance tests for the `fx-market-data` service using **k6**.
These tests include peak load and stress testing, to check system performance.

---
## **How to Run Tests**

FROM GRADLE TASK:
Gradle task 'connectorRun'
- triggers `connectorPerfTest.js`: Simulates peak load conditions on /emitEvent to measure system performance.
Gradle task 'processorRun'
- triggers `processorPerfTest.js`: measures FX rate update latency by sending data on /emitEvent, polling for updates from /fx/rates/{pair}, verifying consistency, and aggregating response time statistics.

From TERMINAL:
- `TEST_MODE=parallel k6 run connectorPerfTest.js` - Runs the test with 120 parallel virtual users, each sending a request with a random currency pair.
- `TEST_MODE=all_in_one k6 run connectorPerfTest.js` - Runs the test once, sending a single request containing all 120 currency pairs.

---


### See the report

Now it's generated as an artifact after performancelocal - Run task.
qa/build/test-results

