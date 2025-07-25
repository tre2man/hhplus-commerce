package kr.hhplus.be.server.domain.balance.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.balance.dto.BalanceResponse;
import kr.hhplus.be.server.domain.balance.dto.ChargeBalanceRequest;
import kr.hhplus.be.server.domain.balance.service.BalanceFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balance")
@Tag(name = "Balance", description = "잔액 API")
public class BalanceController {
    private final BalanceFacade balanceFacade;

    public BalanceController (BalanceFacade balanceFacade) {
        this.balanceFacade = balanceFacade;
    }

    @Operation(summary = "잔고 충전", description = "특정 사용자의 잔고를 충전할 수 있습니다.")
    @ApiResponse(responseCode = "201", description = "잔고 충전 성공")
    @PostMapping("{userId}/charge")
    public ResponseEntity<Void> chargeBalance(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody ChargeBalanceRequest request
    ) {
        this.balanceFacade.chargeBalance(userId, request.getAmount());
        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "잔고 확인", description = "특정 사용자의 잔고를 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "잔고 조회 성공")
    @GetMapping("{userId}")
    public ResponseEntity<BalanceResponse> getBalance(
            @PathVariable("userId") Long userId
    ) {
        Integer amount = this.balanceFacade.getBalance(userId).getAmount();
        return ResponseEntity.ok(BalanceResponse.of(amount));
    }
}
