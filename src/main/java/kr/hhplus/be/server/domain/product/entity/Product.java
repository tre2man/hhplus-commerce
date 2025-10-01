package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.exception.ProductErrorCode;
import kr.hhplus.be.server.exception.DomainException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity(name = "product")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "description")
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Product() {}

    private Product(String name, Integer stock, Integer price, String description) {
        this.name = name;
        this.stock = stock;
        this.price = price;
        this.description = description;
    }

    public static Product create(String name, Integer stock, Integer price, String description) {
        return new Product(name, stock, price, description);
    }

    public void decreaseStock(int quantity) {
        if (this.stock < quantity) {
            throw new DomainException(ProductErrorCode.INSUFFICIENT_STOCK);
        }
        this.stock -= quantity;
    }

    public void increaseStock(int quantity) {
        if (quantity < 0) {
            throw new DomainException(ProductErrorCode.NEGATIVE_STOCK);
        }
        this.stock += quantity;
    }
}
