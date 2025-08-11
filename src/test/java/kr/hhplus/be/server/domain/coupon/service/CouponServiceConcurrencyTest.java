package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.DatabaseClean;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Testcontainers
class CouponServiceConcurrencyTest {
    @Autowired
    CouponService couponService;

    @Autowired
    CouponRepository couponRepository;

    @Autowired
    private DatabaseClean dataBaseClean;

    @BeforeEach
    void setUp() {
        dataBaseClean.execute();
    }

    @DisplayName("[성공] 쿠폰 발급 횟수 차감은 동시에 실행해도 정상적으로 작동해야 한다.")
    @Test
    void 성공_쿠폰_발급횟수_차감() throws InterruptedException {
        // Given
        Long couponId = 1L; // 예시 쿠폰 ID
        Integer discountAmount = 1000;
        Integer totalQuantity = 100;
        Integer expireDay = 30;
        Coupon coupon = Coupon.create(
                "테스트 쿠폰",
                discountAmount,
                totalQuantity,
                expireDay
        );
        couponRepository.save(coupon);

        // When
        Integer threads = 2;
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();
                    couponService.issueCoupon(couponId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        // Then
        Integer totalIssuedQuantity = threads;
        Coupon updatedCoupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        assertThat(updatedCoupon.getIssuedQuantity()).isEqualTo(totalIssuedQuantity);
    }

    @DisplayName("[성공] 쿠폰 발급 횟수 차감이 동시에 실행될 시 최대 발급 가능 수량만큼만 발급이 되어야 한다.")
    @Test
    void 성공_쿠폰_발급횟수_차감제한() throws InterruptedException {
        // Given
        Long couponId = 1L;
        Integer discountAmount = 1000;
        Integer totalQuantity = 1; // 제한된 수량으로 설정
        Integer expireDay = 30;
        Coupon coupon = Coupon.create(
                "테스트 쿠폰",
                discountAmount,
                totalQuantity,
                expireDay
        );
        couponRepository.save(coupon);

        // When
        int threads = 2; // 총 수량보다 많은 스레드
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();
                    couponService.issueCoupon(couponId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }  finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        // Then
        Coupon updatedCoupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        assertThat(updatedCoupon.getIssuedQuantity()).isEqualTo(totalQuantity); // 최대 수량만큼만 발급되어야 함
    }
}
