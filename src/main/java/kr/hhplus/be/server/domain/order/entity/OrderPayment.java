package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity(name = "order_payment")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class OrderPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long orderId;

    @Column(name = "order_amount", nullable = false)
    private Integer orderAmount;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Column(name = "used_amount", nullable = false)
    private Integer usedAmount;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected OrderPayment() {}

    private OrderPayment(Long orderId, Integer orderAmount, Integer discountAmount, Integer usedAmount) {
        this.orderId = orderId;
        this.orderAmount = orderAmount;
        this.discountAmount = discountAmount;
        this.usedAmount = usedAmount;
    }

    public static OrderPayment create(Long orderId, Integer orderAmount, Integer discountAmount, Integer usedAmount) {
        return new OrderPayment(orderId, orderAmount, discountAmount, usedAmount);
    }
}
