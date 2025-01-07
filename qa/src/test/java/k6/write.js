const fs = require('fs');

const eventFilePath = './expected-events.txt';

// Функция для генерации события
function generateEvent(id) {
  return {
    id: `event_${id}`,
    timestamp: new Date().toISOString(),
    userId: `user_${id}`,
    action: 'CREATE',
  };
}

// Генерация и запись событий
function writeEventsToFile(count) {
  const stream = fs.createWriteStream(eventFilePath, { flags: 'w' });

  for (let i = 1; i <= count; i++) {
    const event = generateEvent(i);
    stream.write(JSON.stringify(event) + '\n');
  }

  stream.end();
  console.log(`${count} events written to ${eventFilePath}`);
}

// Генерируем 10 событий
writeEventsToFile(10);