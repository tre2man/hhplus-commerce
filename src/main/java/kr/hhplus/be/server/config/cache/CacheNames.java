package kr.hhplus.be.server.config.cache;


import java.util.List;
import java.util.concurrent.TimeUnit;


public class CacheNames {
    private static final long MINUTE = 60L;

    public static final String POPULAR_PRODUCTS = "PRODUCT::POPULAR";
    private static final long POPULAR_PRODUCTS_EXPIRATION_MIN = 60 * MINUTE;

    public static final String PRODUCT_INFO = "PRODUCT::INFO";
    private static final long PRODUCT_INFO_EXPIRATION_MIN = 60 * MINUTE;

    public static List<CacheName> getAll() {
        return List.of(
                new CacheName(POPULAR_PRODUCTS, POPULAR_PRODUCTS_EXPIRATION_MIN, TimeUnit.MINUTES),
                new CacheName(PRODUCT_INFO, PRODUCT_INFO_EXPIRATION_MIN, TimeUnit.MINUTES)
        );
    }

    public record CacheName(
            String name,
            long expirationTime,
            TimeUnit timeUnit
    ) {
    }
}
