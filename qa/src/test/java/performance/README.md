# Performance Testing with k6

This documentation explains how to run performance tests for the `fx-market-data` service using **k6**.
These tests include peak load and stress testing, to check system performance.

---

## **Prerequisites**

1. **Install k6**:
    - **macOS**:
      ```bash
      brew install k6
      ```
    - **Windows**:
      ```bash
      choco install k6
      ```
    - **Linux**:
      ```bash
      sudo apt-get install k6
      ```
   Or follow the official guide: [k6 Installation](https://k6.io/docs/getting-started/installation/).

2. **Ensure the application is running**:
    - Start the `fx-market-data` application locally (on port `3080`) before running the tests.

---

## **Test Files**

- `peakLoadTest.js`: Simulates peak load conditions to measure system performance.
- `longStandingTest.js`: Gradually increases the load to determine the system's breaking point.

---

## **How to Run Tests**

### 1. Run Peak Load Test
The Peak Load Test evaluates the system's performance under a high number of concurrent users for a short period.

```bash
 K6_WEB_DASHBOARD=true k6 run peakLoadTest.js
```

### 2. See the report

http://127.0.0.1:5665/ui/?endpoint=/

