package kr.hhplus.be.server.domain.dataplatform.service;

import kr.hhplus.be.server.domain.dataplatform.command.GetTopNCommand;
import kr.hhplus.be.server.domain.dataplatform.command.SaveTopNCommand;
import kr.hhplus.be.server.domain.dataplatform.command.SendOrderDataCommand;
import kr.hhplus.be.server.domain.dataplatform.entity.OrderRank;
import kr.hhplus.be.server.domain.dataplatform.entity.OrderRankProduct;
import kr.hhplus.be.server.domain.dataplatform.repository.OrderRankDataRepository;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.product.vo.ProductRankVo;
import kr.hhplus.be.server.domain.product.vo.ProductVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DataPlatformService {
    private final ProductService productService;
    private final OrderRankDataRepository orderRankDataRepository;

    public void sendOrderData(List<SendOrderDataCommand> orderDataCommands) {
        for( SendOrderDataCommand command : orderDataCommands) {
            orderRankDataRepository.incrementDailyCount(
                command.toIncrementDailyCountCommand()
            );
        }
    }

    /**
     * 1. 오늘을 포함한 n일 전까지의 데이터를 조회합니다.
     * 2. n 일간의 주문 데이터를 기반으로 상위 n개의 상품을 계산합니다.
     * 3. 상위 n개의 상품을 Redis에 저장합니다.
     */
    public void updateTopNOrderProducts(int days) {
        LocalDateTime today = LocalDateTime.now();

        Map<Long, Integer> aggregated = new HashMap<>();
        for (int i = 0; i <= days; i++) {
            LocalDateTime date = today.minusDays(i);
            List<OrderRank> dailyRanks = orderRankDataRepository.getTopN(
                    new GetTopNCommand(date, days)
            );
            for (OrderRank rank : dailyRanks) {
                aggregated.merge(rank.productId(), rank.score(), Integer::sum);
            }
        }

        List<ProductVo> products = productService.getProductsByIds(
            aggregated.keySet().stream().toList()
        );
        List<OrderRankProduct> topN = aggregated.entrySet().stream()
            .map(entry -> {
                ProductVo product = products.stream()
                    .filter(p -> p.getId().equals(entry.getKey()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. productId: " + entry.getKey()));
                return new OrderRankProduct(
                        entry.getKey(),
                        product.getName(),
                        product.getPrice(),
                        entry.getValue()
                );
            })
            .sorted((a, b) -> Integer.compare(b.score(), a.score()))
            .limit(days)
            .toList();

        orderRankDataRepository.saveTopN(
                new SaveTopNCommand(days, topN)
        );
    }

    /**
     * 현재 주문건수 상위 n개의 상품을 조회합니다.
     */
    public List<ProductRankVo> getTopNOrderProducts(int n) {
        return orderRankDataRepository.getTopNOrderProducts(n).stream()
                .map(ProductRankVo::of)
                .toList();
    }
}
