package kr.hhplus.be.server.domain.order.command;

import java.util.Comparator;
import java.util.List;

public record OrderCommand(
    List<OrderProductCommand> productCommandList,
    OrderPaymentCommand paymentCommand,
    UseBalanceCommand useBalanceCommand,
    List<UseCouponCommand> useCouponCommandList
) {
    // 동시성 이슈를 방지하기 위해 상품번호를 오름차순으로 정렬
    public List<OrderProductCommand> productCommandList() {
        return productCommandList.stream()
            .sorted(Comparator.comparingLong(OrderProductCommand::productId))
            .toList();
    }

    public List<Long> getProductIds() {
        return productCommandList.stream()
            .map(OrderProductCommand::productId)
            .sorted()
            .toList();
    }
}

