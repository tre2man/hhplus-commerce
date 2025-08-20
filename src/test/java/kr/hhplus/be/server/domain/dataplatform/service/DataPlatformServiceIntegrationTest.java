package kr.hhplus.be.server.domain.dataplatform.service;

import kr.hhplus.be.server.domain.dataplatform.command.CreateOrderDataCommand;
import kr.hhplus.be.server.domain.dataplatform.entity.OrderRank;
import kr.hhplus.be.server.domain.dataplatform.repository.OrderRankDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Testcontainers
class DataPlatformServiceIntegrationTest {
    private DataPlatformService dataPlatformService;

    @Autowired
    private OrderRankDataRepository orderRankDataRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        dataPlatformService = new DataPlatformService(orderRankDataRepository);
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @DisplayName("[성공] 인기상품 조회")
    @Test
    void 성공_인기상품_조회() {
        // given
        List<CreateOrderDataCommand> orderDataCommands = List.of(
            new CreateOrderDataCommand(1L, 10),
            new CreateOrderDataCommand(2L, 20),
            new CreateOrderDataCommand(3L, 30)
        );

        // when
        dataPlatformService.sendOrderData(orderDataCommands);
        dataPlatformService.updateTopNOrderProducts(3);

        // then
        List<OrderRank> topNOrderProducts = dataPlatformService.getTopNOrderProducts(3);
        assertThat(topNOrderProducts).isNotNull();
        assertThat(topNOrderProducts.size()).isEqualTo(3);
        assertThat(topNOrderProducts.get(0).productId()).isEqualTo(3L);
        assertThat(topNOrderProducts.get(1).productId()).isEqualTo(2L);
        assertThat(topNOrderProducts.get(2).productId()).isEqualTo(1L);
        assertThat(topNOrderProducts.get(0).score()).isEqualTo(30);
        assertThat(topNOrderProducts.get(1).score()).isEqualTo(20);
        assertThat(topNOrderProducts.get(2).score()).isEqualTo(10);
    }

    @DisplayName("[실패] 인기상품 업데이트 실패")
    @Test
    void 실패_인기상품_업데이트() {
        // given
        List<CreateOrderDataCommand> orderDataCommands = List.of(
                new CreateOrderDataCommand(1L, 10),
                new CreateOrderDataCommand(2L, 20),
                new CreateOrderDataCommand(3L, 30)
        );

        // when
        dataPlatformService.sendOrderData(orderDataCommands);

        // then
        List<OrderRank> topNOrderProducts = dataPlatformService.getTopNOrderProducts(3);
        assertThat(topNOrderProducts).isNotNull();
        assertThat(topNOrderProducts.size()).isEqualTo(0);
    }
}
