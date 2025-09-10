package kr.hhplus.be.server.domain.coupon.repository;

import kr.hhplus.be.server.config.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

import static java.util.concurrent.TimeUnit.MINUTES;

@Repository
@RequiredArgsConstructor
public class CouponIssueUserRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void createCouponIssueUsers(Long couponId) {
        String key = CacheNames.COUPON_ISSUE_USERS + ":" + couponId;
        redisTemplate.opsForSet().add(key, Set.of());
    }

    public boolean isCouponIssueRequested(Long couponId, Long userId) {
        String key = CacheNames.COUPON_ISSUE_USERS + ":" + couponId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId.toString()));
    }

    public void addCouponIssueRequestUserId(Long couponId, Long userId) {
        String key = CacheNames.COUPON_ISSUE_USERS + ":" + couponId;
        redisTemplate.opsForSet().add(key, userId.toString());
    }
}
