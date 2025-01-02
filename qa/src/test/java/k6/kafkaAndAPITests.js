import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '30s', target: 10 }, ђ
        { duration: '1m', target: 50 },  ђ
        { duration: '30s', target: 0 },  ђ
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], ђ
        http_req_failed: ['rate<0.01'],   ђ
    },
};

const apiEndpoint = 'http://localhost:8082/topics/test-topic';
const consumerBaseUrl = 'http://localhost:8082/consumers';
const headers = { 'Content-Type': 'application/vnd.kafka.json.v2+json' };

const consumerGroup = 'shared-group';
const consumerName = 'shared-consumer';

export function setup() {
    const consumerConfig = {
        name: consumerName,
        format: 'json',
        'auto.offset.reset': 'earliest',
    };

    const createConsumerRes = http.post(
        `${consumerBaseUrl}/${consumerGroup}`,
        JSON.stringify(consumerConfig),
        { headers }
    );

    check(createConsumerRes, {
        'Consumer created (200)': (r) => r.status === 200,
    });

    const subscriptionPayload = JSON.stringify({
        topics: ['test-topic'],
    });

    const subscribeRes = http.post(
        `${consumerBaseUrl}/${consumerGroup}/instances/${consumerName}/subscription`,
        subscriptionPayload,
        { headers }
    );

    check(subscribeRes, {
        'Subscribed to topic (200)': (r) => r.status === 200,
    });

    return { consumerName };
}

export default function (data) {
    // 1. send to Kafka
    const payload = JSON.stringify({
        records: [
            {
                value: {
                    message: `Hello, Kafka! Iteration: ${__ITER}`,
                },
            },
        ],
    });

    const postRes = http.post(apiEndpoint, payload, { headers });

    check(postRes, {
        'Message sent (200)': (r) => r.status === 200,
    });

    // 2. read from Kafka
    const readRes = http.get(
        `${consumerBaseUrl}/${consumerGroup}/instances/${data.consumerName}/records`,
        { headers: { Accept: 'application/vnd.kafka.json.v2+json' } }
    );

    check(readRes, {
        'Messages received': (r) => r.status === 200 && r.body.includes(`Hello, Kafka! Iteration: ${__ITER}`),
    });

    sleep(1);
}

export function teardown(data) {
    http.del(
        `${consumerBaseUrl}/${consumerGroup}/instances/${data.consumerName}`,
        null,
        { headers }
    );
}