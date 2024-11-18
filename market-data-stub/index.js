// app.js
const express = require("express");
const app = express();
const { v4 } = require("uuid");
let clients = [];

app.use(express.json());
app.use(express.static("./public"));

let fxRates = { "USD/JPY": 110.45, "EUR/USD": 1.1357 };

function formatToFourDecimals(num) {
  num = String(num);
  let match = num.match(/^\d+\.0*\d{0,4}/);
  if (match) {
    return match[0];
  }
  return num;
}

function updateFxRates() {
    for (let pair in fxRates) {
        const newRate = Number(fxRates[pair]) + (Math.random() - 0.5) * 0.001;
        fxRates[pair] = formatToFourDecimals(newRate);
    }
}


function sendDataToAllClients(data) {
    clients.forEach((client) =>
        client.response.write(JSON.stringify(data) + "\n")
    );
}

app.get("/subscribe", async (req, res) => {
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
    sendDataToAllClients(fxRates);
    updateFxRates(); // Update data with a random number 
    setTimeout(updateData, 1000); // Schedule next update in 10ms 
}


updateData();

app.listen(3080, () => {
    console.log("Server is running on port 3080");
});
