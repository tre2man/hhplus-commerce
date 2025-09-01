package kr.hhplus.be.server.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity(name = "issued_coupon")
@Getter
@Setter
public class IssuedCoupon {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected IssuedCoupon() {}

    private IssuedCoupon(Long userId, Long couponId, LocalDateTime expireAt) {
        this.userId = userId;
        Coupon couponRef = new Coupon();
        couponRef.setId(couponId);
        this.coupon = couponRef;
        this.expireAt = expireAt;
    }

    public static IssuedCoupon create(Long userId, Long couponId, LocalDateTime expireAt) {
        return new IssuedCoupon(userId, couponId, expireAt);
    }

    public void use() {
        if (this.usedAt != null) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }
        this.usedAt = LocalDateTime.now();
    }
}
