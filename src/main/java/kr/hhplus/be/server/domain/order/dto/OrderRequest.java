package kr.hhplus.be.server.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.product.vo.CreateOrderUseCaseVo;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
@Schema(description = "주문 요청 정보")
public class OrderRequest {
    @Schema(description = "사용자 ID", example = "67890")
    private Long userId;

    @Schema(description = "주문할 상품 목록")
    private List<OrderProductRequest> products;

    @Schema(description = "쿠폰 아이디", nullable = true)
    private Optional<Long> couponId;

    public OrderRequest(Long userId, List<OrderProductRequest> products, Optional<Long> couponId) {
        this.userId = userId;
        this.products = products;
        this.couponId = couponId;
    }

    public CreateOrderUseCaseVo toCreateOrderUseCaseVo() {
        return CreateOrderUseCaseVo.of(
                this.userId,
                this.products.stream()
                        .map(OrderProductRequest::toCreateOrderProductUseCaseVo)
                        .toList(),
                this.couponId
        );
    }
}
