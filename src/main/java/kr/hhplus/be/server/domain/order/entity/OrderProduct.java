package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity(name = "order_product")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    protected OrderProduct() {}

    public static OrderProduct create(
            Long orderId,
            Long productId,
            Integer quantity
    ) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setOrderId(orderId);
        orderProduct.setProductId(productId);
        orderProduct.setQuantity(quantity);
        return orderProduct;
    }
}
