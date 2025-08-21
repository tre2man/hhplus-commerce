package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.command.CreateIssuedCouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponJdbcRepository;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import kr.hhplus.be.server.domain.coupon.vo.UserCouponVo;
import kr.hhplus.be.server.domain.order.command.UseCouponCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssuedCouponService {
    private final IssuedCouponRepository issuedCouponRepository;
    private final IssuedCouponJdbcRepository issuedCouponJdbcRepository;

    public void createIssuedCoupon(CreateIssuedCouponCommand command) {
        IssuedCoupon issuedCoupon = IssuedCoupon.create(
                command.userId(),
                command.couponId(),
                LocalDateTime.now().plusDays(command.expireDays())
        );
        issuedCouponRepository.save(issuedCoupon);
    }

    @Transactional
    public void createBulkIssuedCoupon(List<CreateIssuedCouponCommand> commandList) {
        issuedCouponJdbcRepository.batchInsert(commandList);
    }

    public void useCoupon(List<UseCouponCommand> commandList) {
        for (UseCouponCommand command : commandList) {
            IssuedCoupon issuedCoupon = issuedCouponRepository.findById(command.issuedCouponId())
                    .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));
            issuedCoupon.use();
            issuedCouponRepository.save(issuedCoupon);
        }
    }

    public List<UserCouponVo> getUserCoupons(Long userId) {
        List<IssuedCoupon> issuedCouponList = issuedCouponRepository.findByUserId(userId);
        return issuedCouponList.stream()
                .map(UserCouponVo::of)
                .toList();
    }
}
