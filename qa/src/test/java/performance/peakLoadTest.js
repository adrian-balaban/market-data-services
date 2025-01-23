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

    function getRandomCcyPair() {
        const ccyPairs = [ // Let's simulate similar list of CCY Pairs as Bloomberg publishes
            "EUR/USD", "USD/JPY", "GBP/USD", "AUD/USD", "USD/CAD", "USD/CHF", "NZD/USD", "EUR/JPY",
            "GBP/JPY", "EUR/GBP", "AUD/JPY", "EUR/AUD", "EUR/CHF", "AUD/NZD", "NZD/JPY", "GBP/AUD",
            "GBP/CAD", "EUR/NZD", "AUD/CAD", "GBP/CHF", "AUD/CHF", "EUR/CAD", "CAD/JPY", "GBP/NZD",
            "CAD/CHF", "CHF/JPY", "NZD/CAD", "NZD/CHF", "USD/SEK", "EUR/SEK", "GBP/SEK", "USD/NOK",
            "EUR/NOK", "GBP/NOK", "USD/ZAR", "EUR/ZAR", "GBP/ZAR", "USD/MXN", "EUR/MXN", "GBP/MXN",
            "USD/BRL", "EUR/BRL", "GBP/BRL", "USD/TRY", "EUR/TRY", "GBP/TRY", "USD/INR", "EUR/INR",
            "EUR/INR", "USD/HKD", "EUR/HKD", "GBP/HKD", "USD/SGD", "EUR/SGD", "GBP/SGD", "USD/CNH",
            "EUR/CNH", "GBP/CNH", "USD/KRW", "EUR/KRW", "GBP/KRW", "USD/THB", "EUR/THB", "GBP/THB",
            "USD/IDR", "EUR/IDR", "GBP/IDR", "USD/MYR", "EUR/MYR", "GBP/MYR", "USD/ILS", "EUR/ILS",
            "GBP/ILS", "USD/RUB", "EUR/RUB", "GBP/RUB", "USD/PLN", "EUR/PLN", "GBP/PLN", "USD/HUF",
            "EUR/HUF", "GBP/HUF", "USD/CZK", "EUR/CZK", "GBP/CZK", "USD/DKK", "EUR/DKK", "GBP/DKK",
            "USD/NZD", "EUR/NZD", "GBP/NZD", "USD/TRY", "EUR/TRY", "GBP/TRY", "USD/PKR", "EUR/PKR",
            "GBP/PKR", "USD/ARS", "EUR/ARS", "GBP/ARS", "USD/PHP", "EUR/PHP", "GBP/PHP", "USD/SAR",
            "EUR/SAR", "GBP/SAR", "USD/QAR", "EUR/QAR", "GBP/QAR", "USD/OMR", "EUR/OMR", "GBP/OMR",
            "USD/BHD", "EUR/BHD", "GBP/BHD", "USD/KWD", "EUR/KWD", "GBP/KWD", "USD/AED", "EUR/AED"
        ]

        const randomIndex = Math.floor(Math.random() * ccyPairs.length);
        return ccyPairs[randomIndex];

    }

    function generateRecord() {
        const randomCcyPair = getRandomCcyPair();
        let [baseCurrency, quoteCurrency] = randomCcyPair.split('/');
        return {
            pair: randomCcyPair,
            baseCurrency: baseCurrency,
            quoteCurrency: quoteCurrency,
            ask: (Math.random() * 2).toFixed(4),
            bid: (Math.random() * 2).toFixed(4)
        }
    }

export default function () {

    const payload = JSON.stringify({
        //timestamp: new Date().toISOString(), // If missing stub adds timestamp; which is more accurate for performace test
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

