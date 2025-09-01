package kr.hhplus.be.server.domain.balance.service;

import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;

    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public Optional<Balance> findByUserId(Long userId) {
        return balanceRepository.findByUserId(userId);
    }

    public Balance chargeBalance(Long userId, Integer chargeAmount) {
        Balance balance = this.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("잔고를 찾을 수 없습니다."));
        balance.charge(chargeAmount);
        return balanceRepository.save(balance);
    }

    public Balance useBalance(Long userId, Integer useAmount) {
        Balance balance = this.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("잔고를 찾을 수 없습니다."));
        balance.use(useAmount);
        return balanceRepository.save(balance);
    }

    public boolean hasSufficientBalance(Long userId, Integer amount) {
        Balance balance = this.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("잔고를 찾을 수 없습니다."));
        return balance.getAmount() >= amount;
    }

    public Balance save(Balance balance) {
        return balanceRepository.save(balance);
    }
}
