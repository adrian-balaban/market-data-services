import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

let successRate = new Rate('http_req_success');

export let options = {
    stages: [
        { duration: '5s', target: 1 },
        { duration: '10s', target: 50 },
        { duration: '10s', target: 50 },
        { duration: '5s', target: 1 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<1500'],
        http_req_failed: ['rate<0.05'],
    },
};
let testParam = __ENV.STUB_HOST || "http://localhost:3080";

const url = `${testParam}/emitEvent`;

const payload = JSON.stringify({
    timestamp: new Date().toISOString(),
    rates: [
        { pair: "USD/JPY", baseCurrency: "USD", quoteCurrency: "JPY", ask: "1.5837", bid: "1.5266" },
        { pair: "EUR/USD", baseCurrency: "EUR", quoteCurrency: "USD", ask: "1.1246", bid: "1.0119" },
        { pair: "PLN/USD", baseCurrency: "PLN", quoteCurrency: "USD", ask: "1.9728", bid: "1.411" }
    ]
});

const params = {
    headers: { 'Content-Type': 'application/json' },
};

export default function () {
    let res = http.post(url, payload, params);

    // Track only success rate
    successRate.add(res.status === 200);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 1500ms': (r) => r.timings.duration < 1500,
    });

    sleep(0.5);
}

// Custom Summary
export function handleSummary(data) {
    console.log("\n===== 🏆 Peak Load Test Summary 🏆 =====");
    console.log(`Avg Response Time: ${data.metrics.http_req_duration.values.avg} ms`);
    console.log(`Min Response Time: ${data.metrics.http_req_duration.values.min} ms`);
    console.log(`Max Response Time: ${data.metrics.http_req_duration.values.max} ms`);
    console.log(`95th Percentile: ${data.metrics.http_req_duration.values['p(95)']} ms`);
    console.log(`Success Rate: ${(1 - data.metrics.http_req_failed.values.rate) * 100} %\n`);

}