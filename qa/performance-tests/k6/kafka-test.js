import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '30s', target: 10 },
        { duration: '1m', target: 50 },
        { duration: '30s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.01'],
    },
};

const apiEndpoint = 'http://localhost:8082/topics/test-topic';
const headers = { 'Content-Type': 'application/vnd.kafka.json.v2+json' };

export default function () {
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

    sleep(1);
}