package kr.hhplus.be.server.domain.order.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "order_product")
@Getter
@Setter
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "product_price", nullable = false)
    private Integer productPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    protected OrderProduct() {}

    public static OrderProduct create(Order order, Long productId, Integer productPrice, Integer quantity) {
        OrderProduct op = new OrderProduct();
        op.setOrder(order);
        op.setProductId(productId);
        op.setProductPrice(productPrice);
        op.setQuantity(quantity);
        op.setCreatedAt(LocalDateTime.now());
        return op;
    }
}
