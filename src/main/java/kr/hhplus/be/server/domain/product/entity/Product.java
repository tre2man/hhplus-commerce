package kr.hhplus.be.server.domain.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "product")
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
}
