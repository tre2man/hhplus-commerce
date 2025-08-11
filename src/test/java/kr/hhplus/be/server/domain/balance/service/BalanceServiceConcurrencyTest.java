package kr.hhplus.be.server.domain.balance.service;

import kr.hhplus.be.server.DatabaseClean;
import kr.hhplus.be.server.domain.balance.command.ChargeBalanceCommand;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class BalanceServiceConcurrencyTest {
    @Autowired
    BalanceService balanceService;

    @Autowired
    BalanceRepository balanceRepository;

    @Autowired
    private DatabaseClean dataBaseClean;

    @BeforeEach
    void setUp() {
        dataBaseClean.execute();
    }

    @DisplayName("[성공] 잔액 수정에 대해 낙관적 락이 잘 수행돤다.")
    @Test
    void 성공_잔액_수정_낙관적_락() {
        // Given
        Long userId = 1L;
        Integer initialAmount = 1000;
        Balance balance = Balance.create(userId, initialAmount);
        balanceRepository.save(balance);

        // When
        // 첫 번째 트랜잭션에서 잔액을 조회
        Balance balance1 = balanceRepository.findByUserId(userId).orElseThrow();

        // 두 번째 트랜잭션에서 잔액을 조회
        Balance balance2 = balanceRepository.findByUserId(userId).orElseThrow();

        // 첫 번째 트랜잭션에서 잔액 수정
        balance1.charge(500);
        balanceRepository.save(balance1);

        // Then
        // 두 번째 트랜잭션에서 잔액 수정 시도 (낙관적 락으로 인해 실패해야 함)
        balance2.charge(300);
        assertThatThrownBy(() -> balanceRepository.save(balance2))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @DisplayName("[성공] 낙관적 락으로 인해 잔액 충전이 동시에 실행되어도 최종 잔액은 한번만 업데이트 되어야 한다.")
    @Test
    void 성공_잔액_충전_동시_실행() throws InterruptedException {
        // Given
        Long userId = 1L;
        Integer initialAmount = 1000; // 초기 잔액
        Integer chargeAmount = 500; // 충전 금액
        Balance balance = Balance.create(userId, initialAmount);
        balanceRepository.save(balance);

        // When
        int threads = 2; // 동시 실행할 스레드 수
        AtomicReference<Integer> optimisticLockCount = new AtomicReference<>(0);
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();
                    balanceService.chargeBalance(new ChargeBalanceCommand(userId, chargeAmount));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ObjectOptimisticLockingFailureException e) {
                    // 낙관적 락 예외 발생 시 카운트 증가
                    optimisticLockCount.getAndSet(optimisticLockCount.get() + 1);
                } catch (Exception e) {
                    throw new RuntimeException();
                } finally {
                    doneLatch.countDown();
                }
            });
        }
        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();

        // Then
        Integer expectedBalance = initialAmount + chargeAmount;
        Balance updatedBalance = balanceRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("잔액을 찾을 수 없습니다."));
        // 여러번의 요청이 동시에 들어왔음에도 불구하고 하나의 요청만 수행이 된다.
        assertThat(updatedBalance.getAmount()).isEqualTo(expectedBalance);
        // 1개의 스레드 요청만 성공하고, 나머지 요청은 낙관적 락 예외가 발생한다.
        assertThat(optimisticLockCount.get()).isEqualTo(threads - 1);
    }
}
