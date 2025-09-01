package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.DatabaseClean;
import kr.hhplus.be.server.domain.order.command.OrderProductCommand;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Testcontainers
class ProductServiceConcurrencyTest {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DatabaseClean dataBaseClean;

    @BeforeEach
    void setUp() {
        dataBaseClean.execute();
    }

    @DisplayName("[성공] 상품 재고 감소는 동시에 실행해도 정상적으로 작동해야 한다.")
    @Test
    void 성공_재고감소_동시성() throws InterruptedException {
        // Given
        Integer productStock = 100;
        Integer productPrice = 10000;
        Product product = Product.create(
                "테스트 상품",
                productStock,
                productPrice,
                "테스트 설명"
        );
        this.productRepository.save(product);
        Long productId = product.getId();

        // When
        Integer decreaseQuantity = 1;
        Integer threads = 2;
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();
                    productService.decreaseStock(
                            List.of(new OrderProductCommand(productId, decreaseQuantity))
                    );
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();


        // Then
        // 상품의 재고가 감소했는지 확인
        Product updatedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. productId: " + productId));
        Integer finalStock = productStock - (decreaseQuantity * threads);
        assertThat(updatedProduct.getStock()).isEqualTo( finalStock );
    }

    @DisplayName("[성공] 상품 재고 감소 시 재고가 부족한 경우 정상적으로 예외가 발생해야 한다.")
    @Test
    void 성공_재고감소_재고부족_동시성() throws InterruptedException {
        // Given
        Integer productStock = 1; // 재고가 부족한 상태
        Integer productPrice = 10000;
        Product product = Product.create(
                "테스트 상품",
                productStock,
                productPrice,
                "테스트 설명"
        );
        this.productRepository.save(product);
        Long productId = product.getId();

        // When
        Integer decreaseQuantity = 1;
        Integer threads = 2;
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        AtomicReference<Integer> successCount = new AtomicReference<>(0);
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();
                    productService.decreaseStock(
                            List.of(new OrderProductCommand(productId, decreaseQuantity))
                    );
                    successCount.getAndUpdate(count -> count + 1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();


        // Then
        // 상품의 재고가 감소했는지 확인
        Product updatedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. productId: " + productId));
        Integer finalStock = productStock - (successCount.get() * decreaseQuantity);
        assertThat(updatedProduct.getStock()).isEqualTo(finalStock);
    }
}
