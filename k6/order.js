import http from 'k6/http';

const TOTAL_USERS = 100;

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
    const userId = __VU + 1;
    // 2. 해당 상품의 정보 조회
    // 1~100 중 하나
    const productId = Math.floor(Math.random() * 100) + 1;
    const rawProductDetail = http.get(`http://localhost:8080/product/${productId}`, {
        tags: {name: 'product-detail'},
    });
    const productDetail = JSON.parse(rawProductDetail.body);

    // 3. 해당 상품을 사용하여 주문 진행
    const productPrice = productDetail.price;
    const orderCount = Math.floor(Math.random() * 5) + 1;
    const useAmount = productPrice * orderCount;
    http.post('http://localhost:8080/order', JSON.stringify({
        userId,
        products: [{
            productId,
            quantity: 1,
        }],
        payment: {
            orderAmount: useAmount,
            discountAmount: 0,
            usedAmount: useAmount,
        },
        useBalance: {
            userId,
            useAmount
        }
    }), {
        headers: {
            'Content-Type': 'application/json',
        },
        tags: {name: 'order'},
    });
}