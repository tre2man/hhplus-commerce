package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProductById(Long productId) {
        return this.productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. productId: " + productId));
    }

    public List<Product> getProductsByIds(List<Long> productIds) {
        return this.productRepository.findAllById(productIds);
    }

    public boolean checkStock(Long productId, Integer quantity) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. productId: " + productId));
        return product.getStock() >= quantity;
    }

    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }
}
