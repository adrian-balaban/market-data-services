// app.js
const express = require("express");
const app = express();
const { v4 } = require("uuid");
let clients = [];
let payload = {};
app.use(express.json());
app.use(express.static("./public"));
let record = {  customBody: payload }
//let record = { ...payload }

function sendDataToAllClients(record) {
    if (record['timestamp'] == undefined) {
        record['timestamp'] = new Date().toISOString()
    }
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
app.post("/emitEvent", async (req, res) => {
    record = req.body;
    sendDataToAllClients(record);
    res.send("Sent to SSE:" + '\n' + JSON.stringify(req.body));
});

function updateData() {
    sendDataToAllClients(record);
}
//s
updateData();
app.listen(3081, () => {
    console.log("Server is running on port 3081");
});

