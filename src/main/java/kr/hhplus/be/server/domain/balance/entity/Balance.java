package kr.hhplus.be.server.domain.balance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity(name = "balance")
@Getter
@Setter
public class Balance {
    private static final Integer MAX_AMOUNT = 1_000_000_000;
    private static final Integer MAX_CHARGE_AMOUNT = 5_000_000;
    private static final Integer MIN_AMOUNT = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private String updatedAt;

    @Column(name = "deleted_at")
    private String deletedAt;

    protected Balance() {}

    private Balance(Long userId, Integer amount) {
        if (amount < MIN_AMOUNT || amount > MAX_AMOUNT) {
            throw new IllegalArgumentException("잔액은 " + MIN_AMOUNT + "원 이상, " + MAX_AMOUNT + "원 이하이어야 합니다.");
        }
        this.userId = userId;
        this.amount = amount;
    }

    public static Balance create(Long userId, Integer initialAmount) {
        return new Balance(userId, initialAmount);
    }

    public void charge(Integer chargeAmount) {
        if (chargeAmount < MIN_AMOUNT) {
            throw new IllegalArgumentException("충전 금액은 1원 이상이어야 합니다.");
        }
        if (this.amount + chargeAmount > MAX_AMOUNT) {
            throw new IllegalArgumentException("잔액이 최대 금액을 초과할 수 없습니다.");
        }
        this.amount += chargeAmount;
    }

    public void use(Integer useAmount) {
        if (useAmount < MIN_AMOUNT) {
            throw new IllegalArgumentException("사용 금액은 0원 이상이어야 합니다.");
        }
        if (this.amount - useAmount < MIN_AMOUNT) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.amount -= useAmount;
    }
}
