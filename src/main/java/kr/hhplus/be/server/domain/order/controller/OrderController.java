package kr.hhplus.be.server.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.order.dto.OrderRequest;
import kr.hhplus.be.server.domain.order.service.OrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Tag(name = "Order", description = "주문 API")
@RequiredArgsConstructor
public class OrderController {
    private final OrderFacade orderFacade;

    @PostMapping()
    @Operation(summary = "상품 주문", description = "특정 사용자가 상품을 주문할 수 있습니다.")
    @ApiResponse(responseCode = "201", description = "상품 주문 성공")
    public ResponseEntity<Void> placeOrder(
            @RequestBody OrderRequest orderRequest
    ) {
        this.orderFacade.createOrder(orderRequest.getUserId(), orderRequest.toCommand());
        return ResponseEntity.status(201).build();
    }
}
