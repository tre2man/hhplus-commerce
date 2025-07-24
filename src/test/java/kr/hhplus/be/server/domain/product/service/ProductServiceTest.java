package kr.hhplus.be.server.domain.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

class ProductServiceTest {
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    @DisplayName("[성공] 상품 정보 조회")
    void 상품_정보_조회_성공() {
        // Given
        String name = "상품1";
        String description = "상품1 설명";
        Integer price = 10000;
        Integer amount = 50;

        Product expectedProduct = Product.of(name, description, price, amount);
        when(productRepository.findById(productId)).thenReturn(expectedProduct);

        // When
        Product product = productService.getProductById(productId);

        // Then
        assertThat(product).isEqualTo(expectedProduct);
    }

    @Test
    @DisplayName("[실패] 상품 정보 조회 - 상품 없음")
    void 상품_정보_조회_실패() {
        // Given
        Long productId = 1L;

        // When
        when(productRepository.findById(productId)).thenReturn(null);

        // Then
        Product product = productService.getProduct(productId);
        assertThat(product).isNull();
    }

    @DisplayName("[성공] 상품 재고 있음")
    @ParameterizedTest
    @ValueSource(ints = {10, 20, 50})
    void 상품_재고_있음(int quantity) {
        // Given
        String name = "상품1";
        String description = "상품1 설명";
        Integer price = 10000;
        Integer amount = 50;

        Product product = Product.of(name, description, price, amount);
        when(productRepository.findById(productId)).thenReturn(null);

        // When
        boolean isInStock = productService.checkStock(productId, quantity);

        // Then
        assertThat(isInStock).isFalse();
    }

    @DisplayName("[실패] 상품 재고 없음")
    @ParameterizedTest
    @ValueSource(ints = {51, 100, 200})
    void 상품_재고_없음(int quantity) {
        // Given
        String name = "상품1";
        String description = "상품1 설명";
        Integer price = 10000;
        Integer amount = 50;

        Product product = Product.of(name, description, price, amount);
        when(productRepository.findById(productId)).thenReturn(null);

        // When
        boolean isInStock = productService.checkStock(productId, quantity);

        // Then
        assertThat(isInStock).isFalse();
    }
}
