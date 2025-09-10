package kr.hhplus.be.server.domain.coupon.repository;

import kr.hhplus.be.server.config.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

import static java.util.concurrent.TimeUnit.MINUTES;

@Repository
@RequiredArgsConstructor
public class CouponIssueCountRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void createCouponIssuableCount(Long couponId, Integer issuableCount) {
        String key = CacheNames.COUPON_ISSUABLE_COUNT + ":" + couponId;
        redisTemplate.opsForValue().set(key, issuableCount);
    }

    public Optional<Integer> decrementIssuableCount(Long couponId, Integer count) {
        String key = CacheNames.COUPON_ISSUABLE_COUNT + ":" + couponId;
        return Optional.of(Objects.requireNonNull(redisTemplate.opsForValue().decrement(key, count)).intValue());
    }

    public void setIssuableCount(Long couponId, int count) {
        String key = CacheNames.COUPON_ISSUABLE_COUNT + ":" + couponId;
        redisTemplate.opsForValue().set(key, count);
    }
}
