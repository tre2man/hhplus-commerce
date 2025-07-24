package kr.hhplus.be.server.domain.balance.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class BalanceEntityTest {
    @DisplayName("[성공] 잔액 충전 테스트")
    @ParameterizedTest
    @ValueSource(ints = {0, 1000, 5000, 10000})
    void 잔액_충전_성공(Integer chargeAmount) {
        Long userId = 1L;
        Integer initialAmount = 1000;
        Integer expectAmount = initialAmount + chargeAmount;

        // given
        Balance balanceEntity = Balance.create(userId, initialAmount);

        // when
        balanceEntity.charge(chargeAmount);

        // then
        assertThat(balanceEntity.getAmount()).isEqualTo(expectAmount);
    }

    @DisplayName("[실패] 잔액 충전 테스트 - 음수 금액")
    @ParameterizedTest
    @ValueSource(ints = {-1000, -5000, -10000})
    void 잔액_충전_실패_음수금액(Integer chargeAmount) {
        Long userId = 1L;
        Integer initialAmount = 1000;

        // given
        Balance balance = Balance.create(userId, initialAmount);

        // when, then
        assertThatThrownBy(() -> balance.charge(chargeAmount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[실패] 잔액 충전 테스트 - 1회 최대 금액 초과")
    @ParameterizedTest
    @ValueSource(ints = {5_000_001, 1_000_000_000})
    void 잔액_충전_실패_최대금액_초과(Integer chargeAmount) {
        Long userId = 1L;
        Integer initialAmount = 1000;

        // given
        Balance balance = Balance.create(userId, initialAmount);

        // when, then
        assertThatThrownBy(() -> balance.charge(chargeAmount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[실패] 잔액 충전 테스트 - 누적 최대 금액 초과")
    @ParameterizedTest
    @ValueSource(ints = {1})
    void 잔액_충전_실패_누적최대금액초과(Integer chargeAmount) {
        Long userId = 1L;
        Integer initialAmount = 1_000_000_000;

        // given
        Balance balance = Balance.create(userId, initialAmount);

        // when, then
        assertThatThrownBy(() -> balance.charge(chargeAmount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[성공] 잔액 차감 테스트")
    @ParameterizedTest
    @ValueSource(ints = {0, 1000, 5000, 10000})
    void 잔액_차감_성공(Integer useAmount) {
        Long userId = 1L;
        Integer initialAmount = 10000;
        Integer expectAmount = initialAmount - useAmount;

        // given
        Balance balance = Balance.create(userId, initialAmount);

        // when
        balance.use(useAmount);

        // then
        assertThat(balance.getAmount()).isEqualTo(expectAmount);
    }

    @DisplayName("[실패] 잔액 차감 테스트 - 음수 금액")
    @ParameterizedTest
    @ValueSource(ints = {-1000, -5000, -10000})
    void 잔액_차감_실패_음수금액(Integer useAmount) {
        Long userId = 1L;
        Integer initialAmount = 0;

        // given
        Balance balance = Balance.create(userId, initialAmount);

        // when, then
        assertThatThrownBy(() -> balance.use(useAmount))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[실패] 잔액 차감 테스트 - 잔액 부족")
    @ParameterizedTest
    @ValueSource(ints = {1001, 5_000_000})
    void 잔액_차감_실패_잔액부족(Integer useAmount) {
        Long userId = 1L;
        Integer initialAmount = 0;

        // given
        Balance balance = Balance.create(userId, initialAmount);

        // when, then
        assertThatThrownBy(() -> balance.use(useAmount))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
