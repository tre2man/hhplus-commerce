package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.order.command.OrderProductCommand;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.domain.product.vo.ProductVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Optional<ProductVo> findProductById(Long productId) {
        Optional<Product> product = this.productRepository.findById(productId);
        return product.map(ProductVo::of);
    }

    private Product getProductByIdForUpdate(Long productId) {
        return this.productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. productId: " + productId));
    }

    public List<ProductVo> getProductsByIds(List<Long> productIds) {
        return this.productRepository.findAllById(productIds)
                .stream()
                .map(ProductVo::of)
                .toList();
    }

    public List<ProductVo> getAllProducts() {
        return this.productRepository.findAll()
                .stream()
                .map(ProductVo::of)
                .toList();
    }

    @Transactional
    public void decreaseStock(List<OrderProductCommand> command) {
        for (OrderProductCommand orderProductCommand : command) {
            Long productId = orderProductCommand.productId();
            Product product = this.getProductByIdForUpdate(productId);

            Integer quantity = orderProductCommand.quantity();
            product.decreaseStock(quantity);
            this.productRepository.save(product);
        }
    }
}
