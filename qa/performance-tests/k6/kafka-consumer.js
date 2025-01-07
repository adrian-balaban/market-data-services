const { Kafka } = require('kafkajs');
const fs = require('fs');
const readline = require('readline');

const kafka = new Kafka({
  clientId: 'kafka-consumer',
  brokers: ['localhost:9092'],
});

const groupId = 'test-group';
const consumer = kafka.consumer({
  groupId,
  sessionTimeout: 60000,
});

const expectedEventsFile = './expected-events.txt';

async function readExpectedEvents() {
  return new Promise((resolve) => {
    const events = [];
    const rl = readline.createInterface({
      input: fs.createReadStream(expectedEventsFile),
    });

    rl.on('line', (line) => events.push(line.trim()));
    rl.on('close', () => resolve(events));
  });
}

async function run() {
  const expectedEvents = await readExpectedEvents();
  console.log(`✅ Loaded ${expectedEvents.length} expected events from file.`);

  let receivedEventsCount = 0;
  let verifiedEventsCount = 0;

  await consumer.connect();
  await consumer.subscribe({ topic: 'test-topic1', fromBeginning: false }); // Only new messages

  console.log('🚀 Consumer connected. Checking received events...');

  await consumer.run({
    eachMessage: async ({ message }) => {
      const receivedEventRaw = message.value.toString();

      let innerMessage;
      try {
        // Attempt to parse the raw message; handle objects directly
        innerMessage = typeof receivedEventRaw === 'string' && receivedEventRaw.startsWith('{')
          ? JSON.parse(receivedEventRaw)
          : receivedEventRaw;
      } catch (err) {
        console.error(`❌ Failed to parse message: ${receivedEventRaw}`);
        console.error(`Error: ${err.message}`);
        return;
      }

      console.log(`📥 Received event: ${JSON.stringify(innerMessage)}`);

      const serializedEvent = JSON.stringify(innerMessage);

      const expectedIndex = expectedEvents.indexOf(serializedEvent);
      if (expectedIndex !== -1) {
        console.log(`✅ Event verified: ${serializedEvent}`);
        expectedEvents.splice(expectedIndex, 1); // Remove matched event
        verifiedEventsCount++;
      } else {
        console.error(`❌ Unexpected event: ${serializedEvent}`);
      }

      receivedEventsCount++;

      if (expectedEvents.length === 0) {
        console.log('\n🎉 All expected events verified!');
        console.log(`📊 Total received messages: ${receivedEventsCount}`);
        console.log(`📊 Verified events: ${verifiedEventsCount}`);
        await consumer.stop();
        await consumer.disconnect();
        process.exit(0);
      }
    },
  });

  // Timeout for gracefully stopping the consumer
  setTimeout(async () => {
    console.log('\n⏳ Timeout reached. No more messages to process.');
    console.log(`📊 Final Summary:`);
    console.log(`   - Total received messages: ${receivedEventsCount}`);
    console.log(`   - Verified events: ${verifiedEventsCount}`);
    console.log(`   - Remaining expected events count: ${expectedEvents.length}`);
    if (expectedEvents.length > 0) {
      console.log('   - Remaining expected events:');
      expectedEvents.forEach((event) => console.log(`     ${event}`));
    }
    await consumer.stop();
    await consumer.disconnect();
    process.exit(0);
  }, 10000); // 5-second timeout
}

run().catch(async (err) => {
  console.error('❌ Error in Kafka consumer:', err);
  await consumer.disconnect();
  process.exit(1);
});