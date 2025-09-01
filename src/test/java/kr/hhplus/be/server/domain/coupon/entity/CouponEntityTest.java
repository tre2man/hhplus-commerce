package kr.hhplus.be.server.domain.coupon.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CouponEntityTest {
    @DisplayName("[성공] 쿠폰 생성 테스트")
    @Test
    void 쿠폰_생성_성공() {
        // Given
        String name = "Test Coupon";
        Integer discountAmount = 1000;
        Integer totalQuantity = 100;
        Integer expireDay = 30;

        // When
        Coupon coupon = Coupon.create(name, discountAmount, totalQuantity, expireDay);

        // Then
        assertThat(coupon.getName()).isEqualTo(name);
        assertThat(coupon.getDiscountAmount()).isEqualTo(discountAmount);
        assertThat(coupon.getTotalQuantity()).isEqualTo(totalQuantity);
        assertThat(coupon.getExpireDay()).isEqualTo(expireDay);
    }

    @DisplayName("[성공] 쿠폰 발급 테스트")
    @Test
    void 쿠폰_발급_성공() {
        // Given
        String name = "Test Coupon";
        Integer discountAmount = 1000;
        Integer totalQuantity = 100;
        Integer expireDay = 30;

        Coupon coupon = Coupon.create(name, discountAmount, totalQuantity, expireDay);

        // When
        coupon.issue();

        // Then
        assertThat(coupon.getIssuedQuantity()).isEqualTo(1);
    }

    @DisplayName("[실패] 쿠폰 발급 테스트 - 발급 수량 초과")
    @Test
    void 쿠폰_발급_실패_발급수량초과() {
        // Given
        String name = "Test Coupon";
        Integer discountAmount = 1000;
        Integer totalQuantity = 0;
        Integer expireDay = 30;

        Coupon coupon = Coupon.create(name, discountAmount, totalQuantity, expireDay);

        // When, Then
        assertThatThrownBy(coupon::issue)
                .isInstanceOf(IllegalStateException.class);
    }
}
