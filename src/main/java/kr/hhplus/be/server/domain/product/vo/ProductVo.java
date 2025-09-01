package kr.hhplus.be.server.domain.product.vo;

import kr.hhplus.be.server.domain.product.entity.Product;
import lombok.Getter;

@Getter
public class ProductVo {
    private final Long id;
    private final String name;
    private final Integer price;
    private final Integer stock;

    private ProductVo(Long id, String name, Integer price, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public static ProductVo of(Product product) {
        return new ProductVo(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock()
        );
    }
}
