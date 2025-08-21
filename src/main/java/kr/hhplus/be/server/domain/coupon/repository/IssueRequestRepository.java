package kr.hhplus.be.server.domain.coupon.repository;

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
    private static final String ISSUE_REQUEST_KEY_PREFIX = "COUPON:ISSUE:REQUEST";

    private String getIssueRequestKey(Long couponId) {
        return ISSUE_REQUEST_KEY_PREFIX + ":" + couponId;
    }

    public void addIssueRequest(Long userId, Long couponId) {
        long score = LocalDateTime.now(KST).toInstant(ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now())).toEpochMilli();
        redisTemplate.opsForZSet().add(getIssueRequestKey(couponId), userId, score);
        redisTemplate.expire(getIssueRequestKey(couponId), 5, java.util.concurrent.TimeUnit.SECONDS);
    }

    public void addIssueRequestCouponId(Long couponId) {
        redisTemplate.opsForSet().add(ISSUE_REQUEST_KEY_PREFIX, couponId.toString());
        redisTemplate.expire(ISSUE_REQUEST_KEY_PREFIX, 5, java.util.concurrent.TimeUnit.SECONDS);
    }

    public List<Long> getIssueRequestCouponIdList() {
        Set<Object> members = redisTemplate.opsForSet().members(ISSUE_REQUEST_KEY_PREFIX);
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
