import http from 'k6/http';

const TOTAL_USERS = 1000;

export const options  = {
    scenarios: {
        fixed_rps_test: {
            executor: 'ramping-arrival-rate',
            startRate: 0,
            timeUnit: '1s',
            preAllocatedVUs: 100,
            maxVUs: 100,
            stages: [
                { duration: '10s', target: TOTAL_USERS },
                { duration: '1m', target: TOTAL_USERS },
                { duration: '10s', target: 0 },
            ],
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<500'],
        http_req_failed: ['rate<0.05'],
    },
}

export default function () {
    http.get('http://localhost:8080/product/popular', {
        tags: {name: 'popular-product'},
    });
}