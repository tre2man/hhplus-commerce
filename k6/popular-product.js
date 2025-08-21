import http from 'k6/http';

import { sleep } from 'k6';

export let options  = {
    stages: [
        {duration: '10s', target: 800},
        {duration: '1m', target: 1000},
        {duration: '10s', target: 800},
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.05'],
    },
}

export default function () {
    http.get('http://localhost:8080/product/popular', {
        tags: {name: 'popular-product'},
    });

    sleep(1);
}