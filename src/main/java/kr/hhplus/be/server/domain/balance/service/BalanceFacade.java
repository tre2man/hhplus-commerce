package kr.hhplus.be.server.domain.balance.service;

import kr.hhplus.be.server.domain.balance.entity.Balance;
import org.springframework.stereotype.Service;

@Service
public class BalanceFacade {
    private final BalanceService balanceService;

    public BalanceFacade(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    public Balance getBalance(Long userId) {
        return balanceService.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("잔액을 찾을 수 없습니다."));
    }

    public Balance useBalance(Long userId, Integer amount) {
        return this.balanceService.useBalance(userId, amount);
    }

    public void chargeBalance(Long userId, Integer chargeAmount) {
        this.balanceService.chargeBalance(userId, chargeAmount);
    }
}
