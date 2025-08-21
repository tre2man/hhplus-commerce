package kr.hhplus.be.server.domain.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.coupon.dto.IssueCouponRequest;
import kr.hhplus.be.server.domain.coupon.dto.UserCouponResponse;
import kr.hhplus.be.server.domain.coupon.facade.CouponFacade;
import kr.hhplus.be.server.domain.coupon.vo.UserCouponVo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupon")
@Tag(name = "Coupon", description = "쿠폰 API")
@RequiredArgsConstructor
public class CouponController {
    private final CouponFacade couponFacade;

    @PostMapping()
    @Operation(summary = "쿠폰 발급", description = "사용자가 쿠폰을 발급받습니다.")
    public ResponseEntity<Void> issueCoupon(
            @RequestBody IssueCouponRequest issueCouponRequest
    ) {
        couponFacade.addIssueRequest(issueCouponRequest.getUserId(), issueCouponRequest.getCouponId());
        return ResponseEntity.status(201).build();
    }

    @GetMapping()
    @Operation(summary = "쿠폰 목록 조회", description = "사용자가 보유한 쿠폰 목록을 조회합니다.")
    public ResponseEntity<List<UserCouponResponse>> getCoupons(
            @RequestParam(value = "userId", required = false) Long userId
    ) {
        List<UserCouponVo> userCouponList = couponFacade.getUserCoupons(userId);
        return ResponseEntity.ok(userCouponList.stream()
                .map(UserCouponVo::toResponse)
                .toList());
    }
}
