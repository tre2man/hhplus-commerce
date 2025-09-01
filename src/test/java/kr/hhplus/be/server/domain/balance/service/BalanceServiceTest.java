package kr.hhplus.be.server.domain.balance.service;

import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

class BalanceServiceTest {
    private BalanceService balanceService;

    @Mock
    private BalanceRepository balanceRepository;

    @BeforeEach
    void setup() {
        balanceService = new BalanceService(balanceRepository);
    }

    @DisplayName("[성공] 잔액 추가 성공")
    @ParameterizedTest
    @ValueSource(ints = { 0, 5_000_000 })
    void 잔액_추가_성공(Integer addAmount) {
        // Given
        Long userId = 1L;
        Integer amount = 0;
        Integer expectedAmount = amount + addAmount;

        Balance balance = Balance.create(userId, amount);
        when(this.balanceRepository.findByUserId(userId)).thenReturn(Optional.of(balance));
        when(this.balanceRepository.save(balance)).thenReturn(Balance.create(userId, expectedAmount));

        // When
        Balance updatedBalance = balanceService.useBalance(userId, addAmount);

        // Then
        assertThat(updatedBalance.getAmount()).isEqualTo(expectedAmount);
    }

    @DisplayName("[실패] 잔액 추가 실패 - 잔액을 찾을 수 없음")
    @Test
    void 잔액_추가_실패_잔액을_찾을_수_없음() {
        // Given
        Long userId = 1L;

        when(balanceService.findByUserId(userId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> {
            balanceRepository.findByUserId(userId);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[성공] 잔액 사용 성공")
    @ParameterizedTest
    @ValueSource(ints = { 0, 5_000_000 })
    void 잔액_사용_성공(Integer useAmount) {
        // Given
        Long userId = 1L;
        Integer initialAmount = 5_000_000;
        Integer expectedAmount = initialAmount - useAmount;

        Balance balance = Balance.create(userId, initialAmount);
        when(this.balanceRepository.findByUserId(userId)).thenReturn(Optional.of(balance));
        when(this.balanceRepository.save(balance)).thenReturn(Balance.create(userId, expectedAmount));

        // When
        Balance updatedBalance = balanceService.chargeBalance(userId, useAmount);

        // Then
        assertThat(updatedBalance.getAmount()).isEqualTo(expectedAmount);
    }

    @DisplayName("[실패] 잔액 사용 실패 - 잔액을 찾을 수 없음")
    @Test
    void 잔액_사용_실패_잔액을_찾을_수_없음() {
        // Given
        Long userId = 1L;

        when(balanceService.findByUserId(userId)).thenReturn(Optional.empty());

        // When, Then
        assertThatThrownBy(() -> {
            balanceRepository.findByUserId(userId);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
