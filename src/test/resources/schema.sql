CREATE TABLE user (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      left_at DATETIME,
                      created_at DATETIME NOT NULL,
                      updated_at DATETIME NOT NULL
);

CREATE TABLE product (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL COMMENT '상품 이름',
                         stock INT NOT NULL COMMENT '재고',
                         price BIGINT NOT NULL COMMENT '상품 가격',
                         description TEXT COMMENT '상품 상세설명',
                         created_at DATETIME NOT NULL,
                         updated_at DATETIME NOT NULL,
                         deleted_at DATETIME
);

CREATE TABLE balance (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         amount BIGINT NOT NULL COMMENT '잔고',
                         created_at DATETIME NOT NULL,
                         updated_at DATETIME NOT NULL,
                         deleted_at DATETIME
);

CREATE TABLE balance_history (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 balance_id BIGINT NOT NULL,
                                 amount_changed BIGINT NOT NULL,
                                 transaction_type VARCHAR(50) NOT NULL COMMENT 'CHARGE, USE',
                                 created_at DATETIME NOT NULL
);

CREATE TABLE coupon (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        discount_amount BIGINT NOT NULL COMMENT '할인할 가격',
                        total_quantity INT NOT NULL COMMENT '전체 쿠폰의 수',
                        issued_quantity INT NOT NULL COMMENT '발행된 쿠폰의 수',
                        expire_day INT NOT NULL COMMENT 'N일 후 만료',
                        created_at DATETIME NOT NULL,
                        updated_at DATETIME NOT NULL,
                        deleted_at DATETIME
);

CREATE TABLE issued_coupon (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               coupon_id BIGINT NOT NULL,
                               expire_at DATETIME NOT NULL,
                               used_at DATETIME,
                               created_at DATETIME NOT NULL,
                               updated_at DATETIME NOT NULL,
                               deleted_at DATETIME
);

CREATE TABLE `order` (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         issued_coupon_id BIGINT,
                         discount_amount BIGINT NOT NULL COMMENT '쿠폰의 할인 가격',
                         fail_reason VARCHAR(255) COMMENT '실패 사유',
                         created_at DATETIME NOT NULL
);

CREATE TABLE order_product (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               order_id BIGINT NOT NULL,
                               product_id BIGINT NOT NULL,
                               product_price BIGINT NOT NULL COMMENT '상품 주문 당시 가격',
                               quantity BIGINT NOT NULL COMMENT '구매 수량',
                               created_at DATETIME NOT NULL
);

create TABLE order_payment (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               order_id BIGINT NOT NULL,
                               order_amount BIGINT NOT NULL COMMENT '결제 금액',
                               discount_amount BIGINT NOT NULL COMMENT '할인 금액',
                               used_amount BIGINT NOT NULL COMMENT '사용한 금액',
                               created_at DATETIME NOT NULL
);