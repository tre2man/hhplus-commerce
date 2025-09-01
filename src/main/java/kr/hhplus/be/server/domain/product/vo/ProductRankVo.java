package kr.hhplus.be.server.domain.product.vo;

import kr.hhplus.be.server.domain.dataplatform.entity.OrderRankProduct;
import lombok.Getter;

@Getter
public class ProductRankVo {
    private final Long id;
    private final String name;
    private final Integer price;
    private final Integer orderCount;

    private ProductRankVo(Long id, String name, Integer price, Integer orderCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.orderCount = orderCount;
    }

    public static ProductRankVo of(OrderRankProduct orderRankProduct) {
        return new ProductRankVo(
                orderRankProduct.productId(),
                orderRankProduct.name(),
                orderRankProduct.price(),
                orderRankProduct.score()
        );
    }
}
