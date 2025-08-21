package kr.hhplus.be.server.domain.coupon.repository;

import kr.hhplus.be.server.domain.coupon.command.CreateIssuedCouponCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.sql.Timestamp;

@Repository
@RequiredArgsConstructor
public class IssuedCouponJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_INSERT =
            "INSERT INTO issued_coupon (user_id, coupon_id, expire_at, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?)";

    public void batchInsert(List<CreateIssuedCouponCommand> commands) {
        if (commands.isEmpty()) {
            return;
        }

        final int batchSize = 1000;
        final LocalDateTime now = LocalDateTime.now();

        jdbcTemplate.batchUpdate(
                SQL_INSERT,
                commands,
                batchSize,
                (ps, cmd) -> {
                    Integer days = cmd.expireDays(); // null 방지 체크 권장
                    LocalDateTime expireAt = now.plusDays(days);
                    ps.setLong(1, cmd.userId());
                    ps.setLong(2, cmd.couponId());
                    ps.setTimestamp(3, Timestamp.valueOf(expireAt));
                    ps.setTimestamp(4, Timestamp.valueOf(now));
                    ps.setTimestamp(5, Timestamp.valueOf(now));
                }
        );
    }
}
