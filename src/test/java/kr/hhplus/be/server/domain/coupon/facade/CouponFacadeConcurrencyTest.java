package kr.hhplus.be.server.domain.coupon.facade;

import kr.hhplus.be.server.DatabaseClean;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Testcontainers
class CouponFacadeConcurrencyTest {
    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

    @Autowired
    private DatabaseClean dataBaseClean;


    List<Coupon> InitialSetup() {
        // 초기 쿠폰 데이터 설정
        Coupon coupon1 = Coupon.create("할인 쿠폰", 1000, 5, 7);
        Coupon coupon2 = Coupon.create("무료 쿠폰", 500, 10 , 30);

        // 쿠폰 저장
        couponRepository.save(coupon1);
        couponRepository.save(coupon2);

        return List.of(coupon1, coupon2);
    }

    @BeforeEach
    void setUp(){
        dataBaseClean.execute();
    }


    @DisplayName("[성공] 동시에 쿠폰 발급 시도가 이루어져도 정상적으로 작동해야 한다.")
    @Test
    void 성공_쿠폰_발급_동시성() throws InterruptedException {
        // Given
        List<Coupon> coupons = InitialSetup();
        Coupon coupon = coupons.get(0); // 할인 쿠폰 (5개 발급 가능)
        Long userId = 1L;

        // When
        int threads = 5;
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            Long currentUserId = userId + i; // 각 스레드마다 다른 유저 ID 사용
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();
                    couponFacade.issueCoupon(currentUserId, coupon.getId());
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
        Integer expectedIssuedQuantity = threads; // 5개 발급되어야 함
        Coupon updatedCoupon = couponRepository.findById(coupon.getId())
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        // 발급된 쿠폰의 수량 검증
        assertThat(updatedCoupon.getIssuedQuantity()).isEqualTo(expectedIssuedQuantity);
        // 발급된 쿠폰에 대한 검증
        Long issuedCouponCount = issuedCouponRepository.count();
        assertThat(issuedCouponCount).isEqualTo(expectedIssuedQuantity.longValue());
    }

    @DisplayName("[성공] 동시에 쿠폰 발급 시도가 이루어져도 발급 가능한 수량만큼만 발급되어야 한다.")
    @Test
    void 성공_쿠폰_발급_동시성_제한_테스트() throws InterruptedException {
        // Given
        List<Coupon> coupons = InitialSetup();
        Coupon coupon = coupons.get(0); // 할인 쿠폰 (5개 발급 가능)
        Long userId = 1L;

        // When
        int threads = 10; // 10개의 스레드가 동시에 발급 시도
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < threads; i++) {
            Long currentUserId = userId + i; // 각 스레드마다 다른 유저 ID 사용
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();
                    couponFacade.issueCoupon(currentUserId, coupon.getId());
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
        // 발급 수량 검증
        Integer expectedIssuedQuantity = coupon.getTotalQuantity();
        Coupon updatedCoupon = couponRepository.findById(coupon.getId())
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));
        assertThat(updatedCoupon.getIssuedQuantity()).isEqualTo(expectedIssuedQuantity);

        // 발급된 쿠폰 수량에 대한 검증
        Long issuedCouponCount = issuedCouponRepository.count();
        assertThat(issuedCouponCount).isEqualTo(expectedIssuedQuantity.longValue());
    }
}
