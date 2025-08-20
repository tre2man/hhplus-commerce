package kr.hhplus.be.server.scheduler;

import kr.hhplus.be.server.domain.dataplatform.service.DataPlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularProductCacheScheduler {
    private final DataPlatformService dataPlatformService;

    // 매일 10분마다 인기 상품 캐시를 업데이트합니다.
    @Scheduled(cron = "0 0/10 * * * ?")
    public void updatePopularProductCache() {
        dataPlatformService.updateTopNOrderProducts(5);
    }
}
