package kr.hhplus.be.server.domain.product.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(Long id);
}
