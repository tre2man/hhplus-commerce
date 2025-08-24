package kr.hhplus.be.server.domain.dataplatform.command;

import kr.hhplus.be.server.domain.dataplatform.entity.OrderRankProduct;

import java.util.List;

public record SaveTopNCommand(
    int days,
    List<OrderRankProduct> orderRankProducts
) {
}
