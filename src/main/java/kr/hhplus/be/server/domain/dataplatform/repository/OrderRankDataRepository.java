package kr.hhplus.be.server.domain.dataplatform.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.config.cache.CacheNames;
import kr.hhplus.be.server.domain.dataplatform.command.GetTopNCommand;
import kr.hhplus.be.server.domain.dataplatform.command.IncrementDailyCountCommand;
import kr.hhplus.be.server.domain.dataplatform.command.SaveTopNCommand;
import kr.hhplus.be.server.domain.dataplatform.entity.OrderRank;
import kr.hhplus.be.server.domain.dataplatform.entity.OrderRankProduct;
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
        return CacheNames.RANK_ORDER_DAY + ":" + d.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private String cacheKeyTopN(int n) {
        return CacheNames.RANK_ORDER_RESULT + ":" + n;
    }

    public void incrementDailyCount(IncrementDailyCountCommand command) {
        String key = dayKey(LocalDateTime.now(KST));
        redisTemplate.opsForZSet().incrementScore(key, String.valueOf(command.productId()), command.count());
        redisTemplate.expire(key, Duration.ofDays(CacheNames.RANK_ORDER_DAY_EXPIRATION_MIN));
    }

    /**
     * 특정 날짜의 상위 n개의 데이터를 반환합니다.
     * TODO: 비즈니스 로직 분리 필요
     */
    public List<OrderRank> getTopN(GetTopNCommand command) {
        String key = dayKey(command.date());
        return Objects.requireNonNull(Objects.requireNonNull(redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, command.day() - 1))
                .stream()
                .map(entry -> new OrderRank(
                        Long.parseLong((String) Objects.requireNonNull(entry.getValue())),
                        Objects.requireNonNull(entry.getScore()).intValue()
                ))
                .toList());
    }

    // 주문건수 상위 n개의 정보를 저장합니다.
    public void saveTopN(SaveTopNCommand command) {
        String key = cacheKeyTopN(command.days());
        redisTemplate.opsForValue().set(key, command.orderRankProducts(), Duration.ofDays(CacheNames.RANK_ORDER_RESULT_EXPIRATION_MIN));
    }


    // 현재 주문건수 상위 n개의 정보를 조회합니다.
    public List<OrderRankProduct> getTopNOrderProducts(int days) {
        String key = cacheKeyTopN(days);
        Object raw = redisTemplate.opsForValue().get(key);
        if (raw == null) {
            return List.of();
        }
        return objectMapper.convertValue(
                raw,
                new TypeReference<>() {}
        );
    }
}
