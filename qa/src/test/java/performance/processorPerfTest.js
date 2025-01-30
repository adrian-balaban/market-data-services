import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

let successRate = new Rate('http_req_success');

export let options = {
    iterations: 5,  // count of iterations
    thresholds: {
        http_req_duration: ['p(95)<1500'],
        http_req_failed: ['rate<0.05'],
    },
};

let testParam = __ENV.STUB_HOST || "http://localhost:3080";
const urlEmitEvent = `${testParam}/emitEvent`;
const urlFxRatesBase = "http://localhost:4080/fx/rates/";

const params = {
    headers: { 'Content-Type': 'application/json' },
};

// list of currency pairs

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


// function for generate random `ask` and `bid`
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
    const rates = pairs.map(generateRateObject);
    const ratesObject = { rates: rates };

    // 2. save moment of request send for each pair
    const sendTimestamps = {};
    pairs.forEach(pair => sendTimestamps[pair] = new Date().getTime());

    // 3. log data what we send
    console.log(`🚀 Sending /emitEvent payload:\n${JSON.stringify(ratesObject, null, 2)}`);

    // 4. send post request to /emitEvent (ALL PAIRS IN ONE REQUEST)
    let resEmitEvent = http.post(urlEmitEvent, JSON.stringify(ratesObject), {
        headers: { 'Content-Type': 'application/json' },
        tags: { name: "emitEvent" }
    });

    successRate.add(resEmitEvent.status === 200);

    check(resEmitEvent, {
        'emitEvent status is 200': (r) => r.status === 200,
        'emitEvent response time < 1500ms': (r) => r.timings.duration < 1500,
    });

    // 5. wait 1 second before first get request
   // sleep(1);

    let maxAttempts = 10;
    let attempts = 0;
    let updatedRates = {}; // store updated time for each pair

    // 6. start parallel GET-requests with http.batch()
    while (attempts < maxAttempts) {
        let remainingPairs = pairs.filter(pair => !(pair in updatedRates));

        if (remainingPairs.length === 0) {
            break; // all pairs updated, exit loop
        }

        let batchRequests = remainingPairs.map(pair => {
            let url = `${urlFxRatesBase}${pair.replace('/', '')}`;
            return {
                method: 'GET',
                url: url,
                params: {
                    headers: { 'Content-Type': 'application/json' },
                    tags: { name: "fxRates" }
                }
            };
        });

        let responses = http.batch(batchRequests);

        responses.forEach((response, index) => {
            let pair = remainingPairs[index];
            successRate.add(response.status === 200);

            check(response, {
                [`fxRates ${pair} status is 200`]: (r) => r.status === 200,
                [`fxRates ${pair} response time < 1500ms`]: (r) => r.timings.duration < 1500,
            });

            if (response.status === 200) {
                let responseBody = JSON.parse(response.body);
                let sentRate = rates.find(r => r.pair === pair);

                // check if rates updated
                if (responseBody.ask === sentRate.ask && responseBody.bid === sentRate.bid) {

                    // calculate delta between send and receive moment
                    let receivedAt = new Date().getTime();
                    let delta = receivedAt - sendTimestamps[pair];
                    console.log(`${pair} update time: ${(delta / 1000).toFixed(2)} seconds`);

                    updatedRates[pair] = delta; // save delta
                } else {
                    console.log(`Attempt ${attempts + 1} for ${pair}: Rates do not match. Retrying...`);
                }
            }
        });

        attempts++;
   //     sleep(0.5);  // wait 0.5 sec before next try
    }

    // check if any pairs still not updated
    pairs.forEach(pair => {
        if (!(pair in updatedRates)) {
            console.log(` Failed to get updated rate for ${pair} after ${maxAttempts} attempts.`);
        }
    });

    sleep(1); // waiting before next iteration
}