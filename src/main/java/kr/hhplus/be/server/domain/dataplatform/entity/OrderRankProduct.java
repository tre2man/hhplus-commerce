package kr.hhplus.be.server.domain.dataplatform.entity;

public record OrderRankProduct(
        Long productId,
        String name,
        Integer price,
        Integer score
) {
}
