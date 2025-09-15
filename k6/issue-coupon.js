import http from 'k6/http';

export const options = {
    scenarios: {
        constant_rate: {
            executor: 'constant-arrival-rate',
            rate: 1000,
            timeUnit: '1s',
            duration: '1m',
            preAllocatedVUs: 1000,
            maxVUs: 1000,
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<1000'],
        http_req_failed: ['rate<0.05'],
    },
};

const BASE_URL = 'http://localhost:8080';
const COUPON_ID = 9;
const MAX_USER_ID = 100000000;

export default function () {
    // 1~100000000 사이의 랜덤 유저 ID 생성
    const userId = Math.floor(Math.random() * MAX_USER_ID) + 1;

    const payload = JSON.stringify({
        couponId: COUPON_ID,
        userId: userId
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    http.post(`${BASE_URL}/coupon/issue`, payload, params);
}
