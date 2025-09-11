package kr.hhplus.be.server.domain.balance.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.balance.TransactionType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity(name="balance_history")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BalanceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "balance_id", nullable = false)
    private Long balanceId;

    @Column(name = "changed_amount", nullable = false)
    private Integer changedAmount;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    protected BalanceHistory() {}

    private BalanceHistory(Long balanceId, Integer changedAmount, TransactionType transactionType) {
        this.balanceId = balanceId;
        this.changedAmount = changedAmount;
        this.transactionType = transactionType.getDescription();
    }

    public static BalanceHistory create(Long balanceId, Integer changedAmount, TransactionType transactionType) {
        return new BalanceHistory(balanceId, changedAmount, transactionType);
    }
}
