package kr.hhplus.be.server.config.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CacheNames {
    private static final long MINUTE = 60L;

    public static final String POPULAR_PRODUCTS = "PRODUCT::POPULAR";
    public static final long POPULAR_PRODUCTS_EXPIRATION_MIN = 60 * MINUTE;

    public static final String PRODUCT_INFO = "PRODUCT::INFO";
    public static final long PRODUCT_INFO_EXPIRATION_MIN = 60 * MINUTE;

    public static final String ISSUE_REQUEST = "COUPON::ISSUE::REQUEST";
    public static final long ISSUE_REQUEST_EXPIRATION_MIN = 5L;

    public static final String RANK_ORDER_DAY = "RANK::ORDER::DAY";
    public static final long RANK_ORDER_DAY_EXPIRATION_MIN = 4L;

    public static final String RANK_ORDER_RESULT = "RANK::ORDER::RESULT";
    public static final long RANK_ORDER_RESULT_EXPIRATION_MIN = 1L;

    public static List<CacheName> getAll() {
        return List.of(
                new CacheName(POPULAR_PRODUCTS, POPULAR_PRODUCTS_EXPIRATION_MIN, TimeUnit.MINUTES),
                new CacheName(PRODUCT_INFO, PRODUCT_INFO_EXPIRATION_MIN, TimeUnit.MINUTES),
                new CacheName(ISSUE_REQUEST, ISSUE_REQUEST_EXPIRATION_MIN, TimeUnit.SECONDS),
                new CacheName(RANK_ORDER_DAY, RANK_ORDER_DAY_EXPIRATION_MIN, TimeUnit.DAYS)
                , new CacheName(RANK_ORDER_RESULT, RANK_ORDER_RESULT_EXPIRATION_MIN, TimeUnit.DAYS)
        );
    }

    public record CacheName(
            String name,
            long expirationTime,
            TimeUnit timeUnit
    ) {
    }
}
