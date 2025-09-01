import http from 'k6/http';

import { sleep } from 'k6';

const TOTAL_USERS = 100;

export let options  = {
    stages: [
        {duration: '10s', target: 80},
        {duration: '1m', target: 100},
        {duration: '10s', target: 80},
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.05'],
    },
}

export default function () {
    // 상품의 아이디는 1~10 까지 존재한다고 가정
    const productId = Math.floor(Math.random() * 10) + 1;
    http.get(`http://localhost:8080/product/${productId}`, {
        tags: {name: 'product-detail'},
    });
    sleep(1);
}