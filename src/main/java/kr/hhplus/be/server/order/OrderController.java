package kr.hhplus.be.server.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.order.dto.OrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Tag(name = "Order", description = "주문 API")
public class OrderController {
    @PostMapping()
    @Operation(summary = "상품 주문", description = "특정 사용자가 상품을 주문할 수 있습니다.")
    @ApiResponse(responseCode = "201", description = "상품 주문 성공")
    public ResponseEntity<Void> placeOrder(
            @RequestBody OrderRequest orderRequest
    ) {
        // TODO: 주문 처리 로직 구현
        return ResponseEntity.status(201).build();
    }
}
