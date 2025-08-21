package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity(name = "coupon")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "issued_quantity", nullable = false)
    private Integer issuedQuantity;

    @Column(name = "expire_day", nullable = false)
    private Integer expireDay;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Coupon() {}

    private Coupon(String name, Integer discountAmount, Integer totalQuantity, Integer expireDay) {
        this.name = name;
        this.discountAmount = discountAmount;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = 0;
        this.expireDay = expireDay;
    }

    public static Coupon create(String name, Integer discountAmount, Integer totalQuantity, Integer expireDay) {
        return new Coupon(name, discountAmount, totalQuantity, expireDay);
    }


    public void issue(Integer count) {
        if (issuedQuantity + count > totalQuantity) {
            throw new IllegalStateException("쿠폰 발급 가능 수량을 초과했습니다.");
        }
        issuedQuantity += count;
    }
}
