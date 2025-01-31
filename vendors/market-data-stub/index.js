// app.js
const express = require("express");
const app = express();
const { v4 } = require("uuid");
let clients = [];
app.use(express.json());
app.use(express.static("./public"));

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

function getRandomCcyPair() {
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
        ask: (Math.random() * 2).toFixed(randomCcyPair.includes("JPY") ? 2 : 4),
        bid: (Math.random() * 2).toFixed(randomCcyPair.includes("JPY") ? 2 : 4),
    }
}

function updateFxRates() {
    let fxRates =[
        generateRecord(),
        generateRecord(),
        generateRecord(),
        generateRecord(),
        generateRecord()
    ];
   return { "timestamp": new Date().toISOString(), rates: [ ...fxRates ] }

}

function sendDataToAllClients(record) {
    record['timestamp'] =  new Date().toISOString();
    clients.forEach((client) => {
            console.log("Client:" + client.id + " message:" + JSON.stringify(record));
            client.response.write(`data: ${JSON.stringify(record)}\n\n`);
        }
    );
}

app.get("/forex/rates", async (req, res) => {
    const clients_id = v4();
    const headers = {
        "Content-Type": "text/event-stream",
        "Cache-Control": "no-cache",
        Connection: "keep-alive",
    };
    res.writeHead(200, headers);
    clients.push({ id: clients_id, response: res });

    // Close the connection when the client disconnects
    req.on("close", () => {
        clients = clients.filter((c) => c.id !== clients_id);
        console.log(`${clients_id} Connection closed`);
        res.end("OK");
    });
});



function updateData() { 
    const record = updateFxRates(); // Update data with a random number 
    sendDataToAllClients(record);
    setTimeout(updateData, 1000); // Schedule next update in 10ms 
}


updateData();

app.listen(3080, () => {
    console.log("Server is running on port 3080");
});
