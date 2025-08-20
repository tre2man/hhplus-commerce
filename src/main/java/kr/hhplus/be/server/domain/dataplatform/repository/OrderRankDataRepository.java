package kr.hhplus.be.server.domain.dataplatform.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.dataplatform.entity.OrderRank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class OrderRankDataRepository  {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private String dayKey(LocalDateTime d) {
        return "RANK:ORDER:DAY:" + d.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private String cacheKeyTopN(int n) {
        return "RANK:ORDER:RESULT:" + n;
    }

    public void incrementDailyCount(Long productId, Integer count) {
        String key = dayKey(LocalDateTime.now(KST));
        redisTemplate.opsForZSet().incrementScore(key, String.valueOf(productId), count);
        redisTemplate.expire(key, Duration.ofDays(4));
    }

    // 특정 날짜의 상위 n개의 데이터를 반환합니다.
    public List<OrderRank> getTopN(LocalDateTime d, int n) {
        String key = dayKey(d);
        return Objects.requireNonNull(Objects.requireNonNull(redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, n - 1))
                .stream()
                .map(entry -> new OrderRank(
                        Long.parseLong((String) Objects.requireNonNull(entry.getValue())),
                        Objects.requireNonNull(entry.getScore()).intValue()
                ))
                .toList());
    }

    // 주문건수 상위 n개의 정보를 저장합니다.
    public void saveTopN(int n, List<OrderRank> orderRanks) {
        String key = cacheKeyTopN(n);
        redisTemplate.opsForValue().set(key, orderRanks, Duration.ofDays(1)); // set + TTL
    }


    // 현재 주문건수 상위 n개의 정보를 조회합니다.
    public List<OrderRank> getTopNOrderProducts(int n) {
        String key = cacheKeyTopN(n);
        Object raw = redisTemplate.opsForValue().get(key);
        if (raw == null) {
            return List.of();
        };

        List<OrderRank> orderRanks = objectMapper.convertValue(
                raw,
                new TypeReference<>() {}
        );
        return orderRanks.stream()
                .sorted((o1, o2) -> Integer.compare(o2.score(), o1.score()))
                .toList();
    }
}
