package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity(name = "`order`")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "fail_reason")
    private String failReason;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected Order() {}

    private Order(Long userId, String failReason) {
        this.userId = userId;
        this.failReason = failReason;
    }

    public static Order create(Long userId, String failReason) {
        return new Order(userId, failReason);
    }
}
