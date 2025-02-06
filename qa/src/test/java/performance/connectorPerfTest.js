import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

let successRate = new Rate('http_req_success');

const testMode = __ENV.TEST_MODE || "parallel";

export let options = testMode === "parallel"
    ? { iterations: 120, vus: 120 }
    : { iterations: 1, vus: 1 };

const testParam = __ENV.STUB_HOST || "http://localhost:3080";
const url = `${testParam}/emitEvent`;

const params = {
    headers: { 'Content-Type': 'application/json' },
};

const ccyPairs = [
    "EUR/USD", "USD/JPY", "GBP/USD", "AUD/USD", "USD/CAD", "USD/CHF", "NZD/USD", "EUR/JPY",
    "GBP/JPY", "EUR/GBP", "AUD/JPY", "EUR/AUD", "EUR/CHF", "AUD/NZD", "NZD/JPY", "GBP/AUD",
    "GBP/CAD", "EUR/NZD", "AUD/CAD", "GBP/CHF", "AUD/CHF", "EUR/CAD", "CAD/JPY", "GBP/NZD",
    "CAD/CHF", "CHF/JPY", "NZD/CAD", "NZD/CHF", "USD/SEK", "EUR/SEK", "GBP/SEK", "USD/NOK",
    "EUR/NOK", "GBP/NOK", "USD/ZAR", "EUR/ZAR", "GBP/ZAR", "USD/MXN", "EUR/MXN", "GBP/MXN",
    "USD/BRL", "EUR/BRL", "GBP/BRL", "USD/TRY", "EUR/TRY", "GBP/TRY", "USD/INR", "EUR/INR",
    "USD/HKD", "EUR/HKD", "GBP/HKD", "USD/SGD", "EUR/SGD", "GBP/SGD", "USD/CNH", "EUR/CNH",
    "GBP/CNH", "USD/KRW", "EUR/KRW", "GBP/KRW", "USD/THB", "EUR/THB", "GBP/THB", "USD/IDR",
    "EUR/IDR", "GBP/IDR", "USD/MYR", "EUR/MYR", "GBP/MYR", "USD/ILS", "EUR/ILS", "GBP/ILS",
    "USD/RUB", "EUR/RUB", "GBP/RUB", "USD/PLN", "EUR/PLN", "GBP/PLN", "USD/HUF", "EUR/HUF",
    "GBP/HUF", "USD/CZK", "EUR/CZK", "GBP/CZK", "USD/DKK", "EUR/DKK", "GBP/DKK", "USD/NZD",
    "EUR/NZD", "GBP/NZD", "USD/PKR", "EUR/PKR", "GBP/PKR", "USD/ARS", "EUR/ARS", "GBP/ARS",
    "USD/PHP", "EUR/PHP", "GBP/PHP", "USD/SAR", "EUR/SAR", "GBP/SAR", "USD/QAR", "EUR/QAR",
    "GBP/QAR", "USD/OMR", "EUR/OMR", "GBP/OMR", "USD/BHD", "EUR/BHD", "GBP/BHD", "USD/KWD",
    "EUR/KWD", "GBP/KWD", "USD/AED", "EUR/AED", "USD/VND", "EUR/VND", "GBP/VND", "USD/TWD",
    "EUR/TWD", "GBP/TWD", "USD/BGN", "EUR/BGN", "GBP/BGN", "USD/EGP", "EUR/EGP", "GBP/EGP"
];

function getRandomCcyPair() {
    return ccyPairs[Math.floor(Math.random() * ccyPairs.length)];
}

function generateRecord(pair) {
    let [baseCurrency, quoteCurrency] = pair.split('/');
    let decimalPlaces = quoteCurrency === "JPY" ? 2 : 4;
    return {
        pair: pair,
        baseCurrency: baseCurrency,
        quoteCurrency: quoteCurrency,
        ask: (Math.random() * 2).toFixed(decimalPlaces),
        bid: (Math.random() * 2).toFixed(decimalPlaces)
    };
}

export default function () {
    let rates;

    if (testMode === "parallel") {
        const singlePair = getRandomCcyPair();
        rates = [generateRecord(singlePair)];
        console.log(`Running test in PARALLEL mode with pair: ${singlePair}`);
    } else {
        rates = ccyPairs.map(pair => generateRecord(pair));
        console.log("Running test in ALL_IN_ONE mode with all currency pairs.");
    }

    const payload = JSON.stringify({ rates });

    console.log(`\n JSON Sent: ${payload}`);

    let res = http.post(url, payload, params);

    successRate.add(res.status === 200);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 1500ms': (r) => r.timings.duration < 1500,
    });

}