import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
                { duration: '1m', target: 200 },
                { duration: '2m', target: 500 },
                { duration: '3m', target: 1000 },
                { duration: '2m', target: 2000 },
                { duration: '3m', target: 5000 },
                { duration: '3m', target: 10000 },
                { duration: '1m', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<1000'],
        http_req_failed: ['rate<0.1'],
    },
};

const url = 'http://localhost:3080/emitEvent';
const payload = JSON.stringify({
    timestamp: "2024-12-18T01:43:02.894",
    rates: [
        { pair: "USD/JPY", baseCurrency: "USD", quoteCurrency: "JPY", ask: "1.5837", bid: "1.5266" },
        { pair: "EUR/USD", baseCurrency: "EUR", quoteCurrency: "USD", ask: "1.1246", bid: "1.0119" },
        { pair: "PLN/USD", baseCurrency: "EUR", quoteCurrency: "USD", ask: "1.9728", bid: "1.411" }
    ]
});

const params = {
    headers: { 'Content-Type': 'application/json' },
};

export default function () {
    const res = http.post(url, payload, params);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 1000ms': (r) => r.timings.duration < 1000,
    });

    sleep(0.5);
}