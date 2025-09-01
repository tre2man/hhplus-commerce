package kr.hhplus.be.server.domain.coupon.facade;

import kr.hhplus.be.server.DatabaseClean;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.vo.UserCouponVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Testcontainers
class CouponFacadeIntegrationTest {
    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponRepository couponRepository;

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

    @DisplayName("[성공] 초기 유저의 쿠폰은 0개")
    @Test
    void issueCouponTest() {
        // Given
        InitialSetup();
        Long userId = 1L;

        // When
        List<UserCouponVo> userCouponVoList = couponFacade.getUserCoupons(userId);

        // Then
        assertThat(userCouponVoList.size()).isEqualTo(0);
    }
}
