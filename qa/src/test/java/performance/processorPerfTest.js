import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

let successRate = new Rate('http_req_success');

export let options = {
    iterations: 5,
    thresholds: {
        http_req_duration: ['p(95)<1500'],
        http_req_failed: ['rate<0.05'],
    },
};

let testMode = __ENV.TEST_MODE || "all_in_one";
let connectorHost = __ENV.STUB_HOST || "http://localhost:3080";
const urlEmitEvent = `${connectorHost}/emitEvent`;

let procHost = __ENV.PROCESSOR_HOST || "http://localhost:4080";
const urlFxRatesBase = `${procHost}/fx/rates/`;

const params = {
    headers: { 'Content-Type': 'application/json' },
};


const pairs = [
    "EUR/USD", "USD/JPY", "GBP/USD", "AUD/USD", "USD/CAD", "USD/CHF", "NZD/USD", "EUR/JPY",
    "GBP/JPY", "EUR/GBP", "AUD/JPY", "EUR/AUD", "EUR/CHF", "AUD/NZD", "NZD/JPY", "GBP/AUD",
    "GBP/CAD",  "AUD/CAD", "GBP/CHF", "AUD/CHF", "EUR/CAD", "CAD/JPY", "GBP/NZD",
    "CAD/CHF", "CHF/JPY", "NZD/CAD", "NZD/CHF", "USD/SEK", "EUR/SEK", "GBP/SEK", "USD/NOK",
    "EUR/NOK", "GBP/NOK", "USD/ZAR", "EUR/ZAR", "GBP/ZAR", "USD/MXN", "EUR/MXN", "GBP/MXN",
    "USD/BRL", "EUR/BRL", "GBP/BRL", "USD/TRY", "EUR/TRY", "GBP/TRY", "USD/INR", "EUR/INR",
    "USD/HKD", "EUR/HKD", "GBP/HKD", "USD/SGD", "EUR/SGD", "GBP/SGD", "USD/CNH", "EUR/CNH",
    "GBP/CNH", "USD/KRW", "EUR/KRW", "GBP/KRW", "USD/THB", "EUR/THB", "GBP/THB", "USD/IDR",
    "EUR/IDR", "GBP/IDR", "USD/MYR", "EUR/MYR", "GBP/MYR", "USD/ILS", "EUR/ILS", "GBP/ILS",
    "USD/RUB", "EUR/RUB", "GBP/RUB", "USD/PLN", "EUR/PLN", "GBP/PLN", "USD/HUF", "EUR/HUF",
    "GBP/HUF", "USD/CZK", "EUR/CZK", "GBP/CZK", "USD/DKK", "EUR/DKK", "GBP/DKK", "USD/NZD",
    "EUR/NZD",  "USD/PKR", "EUR/PKR", "GBP/PKR", "USD/ARS", "EUR/ARS", "GBP/ARS",
    "USD/PHP", "EUR/PHP", "GBP/PHP", "USD/SAR", "EUR/SAR", "GBP/SAR", "USD/QAR", "EUR/QAR",
    "GBP/QAR", "USD/OMR", "EUR/OMR", "GBP/OMR", "USD/BHD", "EUR/BHD", "GBP/BHD", "USD/KWD",
    "EUR/KWD", "GBP/KWD", "USD/AED", "EUR/AED"
];
let sentRates = {};

let globalStats = {
    totalRequests: 0,
    totalDurations: [],
    counts: {}
};

function generateRateObject(pair) {
    let [baseCurrency, quoteCurrency] = pair.split('/');
    return {
        pair: pair,
        baseCurrency: baseCurrency,
        quoteCurrency: quoteCurrency,
        ask: (Math.random() * 2).toFixed(4),
        bid: (Math.random() * 2).toFixed(4)
    };
}

export default function () {
    // 1. generate rates for all currency pairs
    console.log(`\n🔄 Starting Iteration ${__ITER + 1}`);

    const rates = pairs.map(generateRateObject);
    const ratesObject = { rates: rates };

    // 2. save timestamp of request send for each pair
    rates.forEach(rate => {
        sentRates[rate.pair] = rate;
    });

    // 3. Save timestamp of sending request
    const sendTimestamps = {};
    pairs.forEach(pair => sendTimestamps[pair] = new Date().getTime());

    if (testMode === "all_in_one") {
        console.log("Sending ALL pairs in one request...");
        let resEmitEvent = http.post(urlEmitEvent, JSON.stringify(ratesObject), params);
        successRate.add(resEmitEvent.status === 200);
    } else if (testMode === "parallel") {
        console.log("Sending each pair separately...");
        pairs.forEach(pair => {
            let payload = JSON.stringify({ rates: [sentRates[pair]] });
            let res = http.post(urlEmitEvent, payload, params);
            successRate.add(res.status === 200);
            sendTimestamps[pair] = new Date().getTime();
        });
    }

    let maxAttempts = 10;
    let attempts = 0;
    let updatedRates = {}; // store updated time for each pair
    let responseTimes = [];

    while (attempts < maxAttempts) {
        let remainingPairs = pairs.filter(pair => !(pair in updatedRates));
        if (remainingPairs.length === 0) break;

        let batchRequests = remainingPairs.map(pair => ({
            method: 'GET',
            url: `${urlFxRatesBase}${pair.replace('/', '')}`,
            params
        }));

        let responses = http.batch(batchRequests);

        responses.forEach((response, index) => {
            let pair = remainingPairs[index];
            successRate.add(response.status === 200);

            let receivedAt = new Date().getTime();
            let delta = receivedAt - sendTimestamps[pair];

            if (response.status === 200) {
                let responseBody = JSON.parse(response.body);
                let sentRate = sentRates[pair];

                if (parseFloat(responseBody.ask) === parseFloat(sentRate.ask) &&
                    parseFloat(responseBody.bid) === parseFloat(sentRate.bid)) {

                    responseTimes.push(delta);
                    updatedRates[pair] = delta;

                    globalStats.totalRequests++;
                    globalStats.totalDurations.push(delta);
                    globalStats.counts[delta] = (globalStats.counts[delta] || 0) + 1;
                } else {
                    console.log(`⚠️ Attempt ${attempts + 1} for ${pair}: Rates do not match. Retrying...`);
                }
            }
        });
    //    sleep(0.5);
        attempts++;
    }

    pairs.forEach(pair => {
        if (!(pair in updatedRates)) {
            console.log(`⚠️ Failed to get updated rate for ${pair} after ${maxAttempts} attempts.`);
        }
    });

    let totalResponses = responseTimes.length;
    let counts = {};

    responseTimes.forEach(time => {
        counts[time] = (counts[time] || 0) + 1;
    });

    let sortedOutput = Object.entries(counts)
        .map(([time, count]) => ({
            time: Number(time),
            percentage: totalResponses > 0 ? ((count / totalResponses) * 100).toFixed(2) : 0
        }))
        .sort((a, b) => b.percentage - a.percentage)
        .map(entry => `${entry.percentage}% - ${entry.time} ms`);

    console.log(`\n--- ITERATION RESULT (Iteration ${__ITER + 1}) ---`);
    sortedOutput.forEach(line => console.log(line));

    if (__ITER === options.iterations - 1 || options.iterations === 1) {
        console.log("\n--- FINAL GLOBAL STATISTICS ---");

        let totalGlobalResponses = globalStats.totalDurations.length;

        if (totalGlobalResponses === 0) {
            console.log("No response times recorded in global stats.");
            return;
        }

        let sortedGlobalOutput = Object.entries(globalStats.counts)
            .map(([time, count]) => ({
                time: Number(time),
                percentage: totalGlobalResponses > 0 ? ((count / totalGlobalResponses) * 100).toFixed(2) : 0
            }))
            .filter(entry => !isNaN(entry.time) && entry.percentage > 0)
            .sort((a, b) => b.percentage - a.percentage)
            .map(entry => `${entry.percentage}% - ${entry.time} ms`);

        if (sortedGlobalOutput.length === 0) {
            console.log("No response times recorded in global stats.");
        } else {
            sortedGlobalOutput.forEach(line => console.log(line));
        }
    }
}