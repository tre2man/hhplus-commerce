package kr.hhplus.be.server.domain.dataplatform.service;

import kr.hhplus.be.server.domain.dataplatform.command.SendOrderDataCommand;
import kr.hhplus.be.server.domain.dataplatform.repository.OrderRankDataRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.product.vo.ProductRankVo;
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
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        dataPlatformService = new DataPlatformService(
                productService,
                orderRankDataRepository
        );
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @DisplayName("[성공] 인기상품 조회")
    @Test
    void 성공_인기상품_조회() {
        // given
        List<Product> products = List.of(
            Product.create("상품 1", 100, 1000, "상품 1 설명"),
            Product.create("상품 2", 200, 2000, "상품 2 설명"),
            Product.create("상품 3", 300, 3000, "상품 3 설명")
        );
        productRepository.saveAll(products);
        List<SendOrderDataCommand> orderDataCommands = List.of(
            new SendOrderDataCommand(1L, 10),
            new SendOrderDataCommand(2L, 20),
            new SendOrderDataCommand(3L, 30)
        );

        // when
        dataPlatformService.sendOrderData(orderDataCommands);
        dataPlatformService.updateTopNOrderProducts(3);

        // then
        List<ProductRankVo> topNOrderProducts = dataPlatformService.getTopNOrderProducts(3);
        assertThat(topNOrderProducts).isNotNull();
        assertThat(topNOrderProducts.size()).isEqualTo(3);
        assertThat(topNOrderProducts.get(0).getId()).isEqualTo(3L);
        assertThat(topNOrderProducts.get(1).getId()).isEqualTo(2L);
        assertThat(topNOrderProducts.get(2).getId()).isEqualTo(1L);
        assertThat(topNOrderProducts.get(0).getOrderCount()).isEqualTo(30);
        assertThat(topNOrderProducts.get(1).getOrderCount()).isEqualTo(20);
        assertThat(topNOrderProducts.get(2).getOrderCount()).isEqualTo(10);
    }

    @DisplayName("[실패] 인기상품 업데이트 실패")
    @Test
    void 실패_인기상품_업데이트() {
        // given
        List<Product> products = List.of(
                Product.create("상품 1", 100, 1000, "상품 1 설명"),
                Product.create("상품 2", 200, 2000, "상품 2 설명"),
                Product.create("상품 3", 300, 3000, "상품 3 설명")
        );
        productRepository.saveAll(products);
        List<SendOrderDataCommand> orderDataCommands = List.of(
                new SendOrderDataCommand(1L, 10),
                new SendOrderDataCommand(2L, 20),
                new SendOrderDataCommand(3L, 30)
        );

        // when
        dataPlatformService.sendOrderData(orderDataCommands);

        // then
        List<ProductRankVo> topNOrderProducts = dataPlatformService.getTopNOrderProducts(3);
        assertThat(topNOrderProducts).isNotNull();
        assertThat(topNOrderProducts.size()).isEqualTo(0);
    }
}
