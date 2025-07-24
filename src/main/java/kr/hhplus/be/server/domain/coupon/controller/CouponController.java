package kr.hhplus.be.server.domain.coupon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.infrastructure.coupon.dto.GetCouponResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupon")
@Tag(name = "Coupon", description = "쿠폰 API")
public class CouponController {
    @PostMapping("{userId}/issue")
    @Operation(summary = "쿠폰 발급", description = "특정 사용자에게 쿠폰을 발급할 수 있습니다.")
    @ApiResponse(responseCode = "201", description = "쿠폰 발급 성공")
    public ResponseEntity<GetCouponResponse> issueCoupon(
            @PathVariable("userId") Long userId
    ) {
        // TODO: 쿠폰 발급 로직 구현
        return ResponseEntity.status(201).body(
                new GetCouponResponse(1L, "할인 쿠폰", "1000원 할인 쿠폰", 1000L, "2023-09-01")
        );
    }

    @Operation(summary = "쿠폰 조회", description = "특정 사용자의 쿠폰을 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "쿠폰 조회 성공")
    @GetMapping("{userId}")
    public ResponseEntity<List<GetCouponResponse>> getCoupon(
            @PathVariable("userId") Long userId
    ) {
        // TODO: 쿠폰 조회 로직 구현
        return ResponseEntity.ok(
                List.of(
                        new GetCouponResponse(1L, "할인 쿠폰", "1000원 할인 쿠폰", 1000L, "2023-09-01"),
                        new GetCouponResponse(2L, "무료 배송 쿠폰", "무료 배송 쿠폰", 0L, "2023-09-05")
                )
        );
    }
}
