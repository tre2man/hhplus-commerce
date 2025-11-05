package kr.hhplus.be.server.domain.coupon.repository;

import kr.hhplus.be.server.config.cache.CacheNames;
import kr.hhplus.be.server.exception.exception.DomainException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

import static kr.hhplus.be.server.domain.coupon.exception.CouponErrorCode.NOT_FOUND_UPDATED_ISSUABLE_COUNT;

@Repository
@RequiredArgsConstructor
public class CouponIssueCountRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void createCouponIssuableCount(Long couponId, Integer issuableCount) {
        String key = CacheNames.COUPON_ISSUABLE_COUNT + ":" + couponId;
        redisTemplate.opsForValue().set(key, issuableCount);
    }

    public Integer decrementIssuableCount(Long couponId, Integer count) {
        String key = CacheNames.COUPON_ISSUABLE_COUNT + ":" + couponId;
        Long updatedCount = redisTemplate.opsForValue().decrement(key, count);
        if (updatedCount == null) {
            throw new DomainException(NOT_FOUND_UPDATED_ISSUABLE_COUNT);
        }
        return updatedCount.intValue();
    }

    public void setIssuableCount(Long couponId, int count) {
        String key = CacheNames.COUPON_ISSUABLE_COUNT + ":" + couponId;
        redisTemplate.opsForValue().set(key, count);
    }
}
