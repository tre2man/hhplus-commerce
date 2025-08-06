package kr.hhplus.be.server.domain.balance.service;

import kr.hhplus.be.server.domain.balance.TransactionType;
import kr.hhplus.be.server.domain.balance.command.ChargeBalanceCommand;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import kr.hhplus.be.server.domain.balance.repository.BalanceHistoryRepository;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.order.command.UseBalanceCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;

    public Optional<Balance> findByUserId(Long userId) {
        return balanceRepository.findByUserId(userId);
    }

    public void chargeBalance(ChargeBalanceCommand command) {
        Balance balance = this.findByUserId(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("잔고를 찾을 수 없습니다."));
        balance.charge(command.chargeAmount());
        balanceRepository.save(balance);

        BalanceHistory balanceHistory = BalanceHistory.create(
                balance.getId(),
                command.chargeAmount(),
                TransactionType.CHARGE
        );
        balanceHistoryRepository.save(balanceHistory);
    }

    public void useBalance(UseBalanceCommand command) {
        Balance balance = this.findByUserId(command.userId())
                .orElseThrow(() -> new IllegalArgumentException("잔고를 찾을 수 없습니다."));
        balance.use(command.useAmount());
        balanceRepository.save(balance);

        BalanceHistory balanceHistory = BalanceHistory.create(
                balance.getId(),
                command.useAmount(),
                TransactionType.USE
        );
        balanceHistoryRepository.save(balanceHistory);
    }
}
