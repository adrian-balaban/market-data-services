// app.js
const express = require("express");
const app = express();
const { v4 } = require("uuid");
let clients = [];
let customBody = {};
app.use(express.json());
app.use(express.static("./public"));

let fxRates =[
    { 
        "pair": "USD/JPY",
        "baseCurrency": "USD",
        "quoteCurrency": "JPY",
        "ask": 110.45,
        "bid": 108.45
    },
    {
        "pair": "EUR/USD",
        "baseCurrency": "EUR",
        "quoteCurrency": "USD",
        "ask": 1.1357,
        "bid": 1.1337
    }
];
let record = { "timestamp": new Date().toISOString(), rates: [ ...fxRates ] }
let customRecord = { "timestamp": new Date().toISOString(), customBody }

function formatToFourDecimals(num) {
  num = String(num);
  let match = num.match(/^\d+\.0*\d{0,4}/);
  if (match) {
    return match[0];
  }
  return num;
}

function updateFxRates() {
    fxRates.forEach(
        item => {
            const newAsk = Number(item["ask"]) + (Math.random() - 0.5) * 0.001;
            const newBid = Number(item["bid"]) + (Math.random() - 0.5) * 0.001;
            item["ask"] = formatToFourDecimals(newAsk);
            item["bid"] = formatToFourDecimals(newBid);
        }
    )
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

app.post("/emitevent", async (req, res) => {
    customRecord.customBody = req.body;
    sendDataToAllClients(customRecord);
    res.send("Sent to SSE:" + '\n' + JSON.stringify(req.body));
});


function updateData() { 
    sendDataToAllClients(record);
    updateFxRates(); // Update data with a random number 
    setTimeout(updateData, 1000); // Schedule next update in 10ms 
}


updateData();

app.listen(3080, () => {
    console.log("Server is running on port 3080");
});
