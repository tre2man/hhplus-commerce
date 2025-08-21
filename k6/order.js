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
    // 동시에 같은 유저가 요청을 보내는 것을 방지하기 위해 __VU를 사용하여 유저 ID를 생성
    const userId = ((__VU - 1) % TOTAL_USERS) + 1;

    // 1. 유저가 인기상품 조회 (5개)
    const rawProductInfoList = http.get('http://localhost:8080/product/popular', {
        tags: {name: 'popular-product'},
    });

    // 2. 해당 상품의 정보 조회
    const productInfoList = JSON.parse(rawProductInfoList.body);
    const productId = productInfoList[Math.floor(Math.random() * productInfoList.length)].id;
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

    sleep(1);
}