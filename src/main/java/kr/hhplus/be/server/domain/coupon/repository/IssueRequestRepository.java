package kr.hhplus.be.server.domain.coupon.repository;

import kr.hhplus.be.server.config.cache.CacheNames;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class IssueRequestRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private String getIssueRequestKey(Long couponId) {
        return CacheNames.ISSUE_REQUEST + ":" + couponId;
    }

    public void addIssueRequest(Long userId, Long couponId) {
        long score = LocalDateTime.now(KST).toInstant(ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now())).toEpochMilli();
        redisTemplate.opsForZSet().add(getIssueRequestKey(couponId), userId, score);
        redisTemplate.expire(getIssueRequestKey(couponId), CacheNames.ISSUE_REQUEST_EXPIRATION_MIN, java.util.concurrent.TimeUnit.SECONDS);
    }

    public void addIssueRequestCouponId(Long couponId) {
        redisTemplate.opsForSet().add(CacheNames.ISSUE_REQUEST, couponId.toString());
        redisTemplate.expire(CacheNames.ISSUE_REQUEST, CacheNames.ISSUE_REQUEST_EXPIRATION_MIN, java.util.concurrent.TimeUnit.SECONDS);
    }

    public List<Long> getIssueRequestCouponIdList() {
        Set<Object> members = redisTemplate.opsForSet().members(CacheNames.ISSUE_REQUEST);
        if (members == null || members.isEmpty()) {
            return List.of();
        }
        return members.stream()
                .map(member -> Long.parseLong(member.toString()))
                .toList();
    }

    public List<Long> getIssueRequestUserIdList(Long couponId) {
        String key = getIssueRequestKey(couponId);
        Set<Object> members = redisTemplate.opsForZSet().range(key, 0, -1);
        if (members == null || members.isEmpty()) {
            return List.of();
        }
        return members.stream()
                .map(member -> Long.parseLong(member.toString()))
                .toList();
    }
}
