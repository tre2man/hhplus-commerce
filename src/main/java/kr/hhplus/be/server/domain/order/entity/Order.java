package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity(name = "order")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(name = "final_amount", nullable = false)
    private Integer finalAmount;

    @Column(name = "fail_reason")
    private String failReason;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter
    private List<OrderProduct> orderProducts;

    protected Order() {}

    private Order(Long userId, Integer totalAmount, Integer finalAmount, String failReason) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.finalAmount = finalAmount;
        this.failReason = failReason;
    }

    public void addOrderProduct(OrderProduct orderProduct) {
        this.orderProducts.add(orderProduct);
        orderProduct.setOrder(this);
    }

    public static Order create(Long userId, Integer totalAmount, Integer finalAmount, String failReason) {
        return new Order(userId, totalAmount, finalAmount, failReason);
    }
}
