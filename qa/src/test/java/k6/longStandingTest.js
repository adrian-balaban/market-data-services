import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '1m', target: 50 },
        { duration: '5m', target: 1500 },
        { duration: '2m', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<200'],
        http_req_failed: ['rate<0.01'],
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
        'is status 200': (r) => r.status === 200,
        'body is not empty': (r) => r.body && r.body.length > 0,
    });

    sleep(1);
}