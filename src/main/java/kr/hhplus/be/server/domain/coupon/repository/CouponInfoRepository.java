package kr.hhplus.be.server.domain.coupon.repository;

import kr.hhplus.be.server.config.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CouponInfoRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void createCouponInfo(Long couponId, Integer expireDay) {
        String key = CacheNames.COUPON_INFO + ":" + couponId;
        redisTemplate.opsForValue().set(key, expireDay);
    }

    public Optional<Integer> getCouponExpireDay(Long couponId) {
        String key = CacheNames.COUPON_INFO + ":" + couponId;
        Integer expireDay = (Integer) redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(expireDay);
    }
}
