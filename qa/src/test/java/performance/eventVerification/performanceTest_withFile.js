import http from 'k6/http';
import { check } from 'k6';

const kafkaEndpoint = 'http://localhost:8082/topics/test-topic1';
const filePath = 'expected-events.txt'; // Укажите путь к вашему файлу

export let options = {
  vus: 1,
  iterations: 80,
};

// Загружаем данные из файла
let events = open(filePath, 'r')
  .split('\n')
  .filter((line) => line.trim() !== ''); // Удаляем пустые строки

if (events.length === 0) {
  throw new Error('File is empty or cannot be read');
}

console.log(` Loaded ${events.length} events from file.`);

export default function () {
  // Берем событие по очереди
  const currentEvent = events.shift();

  if (!currentEvent) {
    console.error(' No more events to send.');
    return;
  }

  // Подготавливаем payload
  const eventPayload = JSON.stringify({
    records: [
      {
        value: JSON.parse(currentEvent),
      },
    ],
  });

  // Отправляем запрос
  const res = http.post(kafkaEndpoint, eventPayload, {
    headers: { 'Content-Type': 'application/vnd.kafka.json.v2+json' },
  });

  // Проверяем статус ответа
  const success = check(res, {
    'Event sent successfully': (r) => r.status === 200,
  });

  // Логируем результат
  if (success) {
    console.log(`Sent event: ${currentEvent}`);
  } else {
    console.error(`Failed to send event: ${currentEvent}. Status: ${res.status}`);
  }
}