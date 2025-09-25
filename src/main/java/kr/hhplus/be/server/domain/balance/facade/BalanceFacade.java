package kr.hhplus.be.server.domain.balance.facade;

import kr.hhplus.be.server.domain.balance.command.ChargeBalanceCommand;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.exception.BalanceErrorCode;
import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceFacade {
    private final BalanceService balanceService;

    public Balance getBalance(Long userId) {
        return balanceService.findByUserId(userId)
                .orElseThrow(() -> new RestApiException(BalanceErrorCode.NOT_FOUND_BALANCE));
    }

    public void chargeBalance(Long userId, Integer chargeAmount) {
        this.balanceService.chargeBalance(new ChargeBalanceCommand(userId, chargeAmount));
    }
}
