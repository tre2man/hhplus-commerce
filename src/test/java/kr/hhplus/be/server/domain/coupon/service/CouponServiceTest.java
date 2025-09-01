package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {
    CouponService couponService;

    @Mock
    CouponRepository couponRepository;

    @BeforeEach
    void setUp() {
         couponService = new CouponService(couponRepository);
    }

    @DisplayName("[성공] 쿠폰 발급")
    @Test
    void 쿠폰_발급_성공() {
        // Given
        Long couponId = 1L;
        Integer discountAmount = 1000;
        Integer totalQuantity = 100;
        Integer expireDay = 30;
        Coupon coupon = Coupon.create("Test Coupon", discountAmount, totalQuantity, expireDay);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // When
        couponService.issueCoupon(couponId);

        // Then
        assertThat(coupon.getIssuedQuantity()).isEqualTo(1);
    }

    @DisplayName("[실패] 쿠폰 발급 - 발급 가능한 쿠폰이 없음")
    @Test
    void 쿠폰_발급_실패_발급가능쿠폰없음() {
        // Given
        Long couponId = 1L;
        Integer discountAmount = 1000;
        Integer totalQuantity = 0;
        Integer expireDay = 30;
        Coupon coupon = Coupon.create("Test Coupon", discountAmount, totalQuantity, expireDay);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // When, Then
        assertThatThrownBy(() -> couponService.issueCoupon(couponId))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("[성공] 쿠폰 유효 기간 조회")
    @Test
    void 쿠폰_유효기간_조회_성공() {
        // Given
        Long couponId = 1L;
        Integer expireDay = 30;
        Coupon coupon = Coupon.create("Test Coupon", 1000, 100, expireDay);
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // When
        Integer resultExpireDay = couponService.getCouponExpireDay(couponId);

        // Then
        assertThat(resultExpireDay).isEqualTo(expireDay);
    }
}
