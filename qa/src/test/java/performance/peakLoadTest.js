import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

let successRate = new Rate('http_req_success');

export let options = {
    stages: [
        { duration: '10s', target: 1000 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<1500'],
        http_req_failed: ['rate<0.05'],
    },
};
let testParam = __ENV.STUB_HOST || "http://localhost:3080";

const url = `${testParam}/emitEvent`;

const params = {
    headers: { 'Content-Type': 'application/json' },
};

export default function () {
    
    function getRandomCurrency() {
        const currencies = [
            "USD", "EUR", "JPY", "GBP", "AUD",
            "CAD", "CHF", "CNY", "SEK", "NZD",
            "MXN", "SGD", "HKD", "NOK", "KRW",
            "TRY", "INR", "RUB", "BRL", "ZAR"
        ];

        const randomIndex = Math.floor(Math.random() * currencies.length);
        return currencies[randomIndex];
    }

    function generateRecord() {
        const baseCurrency = getRandomCurrency();
        const quoteCurrency = getRandomCurrency();
        return {
            pair: baseCurrency + '/' + quoteCurrency,
            baseCurrency: baseCurrency,
            quoteCurrency: quoteCurrency,
            ask: (Math.random() * 2).toFixed(4),
            bid: (Math.random() * 2).toFixed(4)
        }
    }

    const payload = JSON.stringify({
        timestamp: new Date().toISOString(),
        rates: [
            generateRecord(),
            generateRecord(),
            generateRecord(),
            generateRecord(),
            generateRecord(),
        ]
    });

    let res = http.post(url, payload, params);

    // Track only success rate
    successRate.add(res.status === 200);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 1500ms': (r) => r.timings.duration < 1500,
    });

    sleep(0.5);
}

