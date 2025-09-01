package kr.hhplus.be.server.scheduler;

import kr.hhplus.be.server.domain.product.facade.ProductFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularProductCacheScheduler {
    private final ProductFacade productFacade;
    private final CacheManager cacheManager;

    // 매일 1시간마다 인기 상품 캐시를 갱신합니다.
    @Scheduled(cron = "0 0 * * * ?")
    public void updatePopularProductCache() {
            // 캐시를 무효화합니다.
            Cache existCache = cacheManager.getCache("PRODUCT::POPULAR");
            if (existCache != null) {
                existCache.clear();
            }
            // 인기 상품 캐시를 갱신합니다.
            productFacade.getPopularProducts();
    }
}
