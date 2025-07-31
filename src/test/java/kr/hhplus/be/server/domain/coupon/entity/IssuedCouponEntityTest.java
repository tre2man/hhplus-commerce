package kr.hhplus.be.server.domain.coupon.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class IssuedCouponEntityTest {
    @DisplayName("[성공] 발급된 쿠폰 생성 테스트")
    @Test
    void 발급된_쿠폰_생성_성공() {
        // Given
        Long userId = 1L;
        Long couponId = 1L;
        LocalDateTime expireAt = LocalDateTime.now().plusDays(30);

        // When
        IssuedCoupon issuedCoupon = IssuedCoupon.create(userId, couponId, expireAt);

        // Then
        assertThat(issuedCoupon.getUserId()).isEqualTo(userId);
        assertThat(issuedCoupon.getExpireAt()).isEqualTo(expireAt);
    }

    @DisplayName("[성공] 쿠폰 사용")
    @Test
    void 쿠폰_사용_성공() {
        // Given
        Long userId = 1L;
        Long couponId = 1L;
        LocalDateTime expireAt = LocalDateTime.now().plusDays(30);
        IssuedCoupon issuedCoupon = IssuedCoupon.create(userId, couponId, expireAt);

        // When
        issuedCoupon.use();

        // Then
        assertThat(issuedCoupon.getUsedAt()).isNotNull();
    }

    @DisplayName("[실패] 이미 사용된 쿠폰 사용 시도")
    @Test
    void 쿠폰_사용_실패_이미사용된쿠폰() {
        // Given
        Long userId = 1L;
        Long couponId = 1L;
        LocalDateTime expireAt = LocalDateTime.now().plusDays(30);
        IssuedCoupon issuedCoupon = IssuedCoupon.create(userId, couponId, expireAt);
        issuedCoupon.use(); // 쿠폰 사용

        // When, Then
        assertThatThrownBy(issuedCoupon::use)
                .isInstanceOf(IllegalStateException.class);
    }
}
