package kr.hhplus.be.server.domain.balance.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.balance.exception.BalanceErrorCode;
import kr.hhplus.be.server.exception.DomainException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity(name = "balance")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Balance {
    public static final int MAX_AMOUNT = 1_000_000_000;
    public static final int MAX_CHARGE_AMOUNT = 5_000_000;
    public static final int MIN_AMOUNT = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Version
    private Long version;

    protected Balance() {}

    private Balance(Long userId, Integer amount) {
        if (amount < MIN_AMOUNT) {
            throw new DomainException(BalanceErrorCode.LOW_BALANCE);
        }
        if (amount > MAX_AMOUNT) {
            throw new DomainException(BalanceErrorCode.OVER_BALANCE);
        }
        this.userId = userId;
        this.amount = amount;
    }

    public static Balance create(Long userId, Integer initialAmount) {
        return new Balance(userId, initialAmount);
    }

    public void charge(Integer chargeAmount) {
        if (chargeAmount < MIN_AMOUNT) {
            throw new DomainException(BalanceErrorCode.CHARGE_LOW_BALANCE);
        }
        if (chargeAmount > MAX_CHARGE_AMOUNT) {
            throw new DomainException(BalanceErrorCode.CHARGE_OVER_BALANCE);
        }
        if (this.amount + chargeAmount > MAX_AMOUNT) {
            throw new DomainException(BalanceErrorCode.OVER_BALANCE);
        }
        this.amount += chargeAmount;
    }

    public void use(Integer useAmount) {
        if (useAmount < MIN_AMOUNT) {
            throw new DomainException(BalanceErrorCode.LOW_BALANCE);
        }
        if (this.amount - useAmount < MIN_AMOUNT) {
            throw new DomainException(BalanceErrorCode.LOW_BALANCE);
        }
        this.amount -= useAmount;
    }
}
