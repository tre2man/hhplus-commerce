package kr.hhplus.be.server.domain.dataPlatform.service;

import kr.hhplus.be.server.domain.order.entity.Order;
import org.springframework.stereotype.Service;

@Service
public class DataPlatformService {
    public void sendOrderData(Order order) {
        // TODO: 구현 필요
        System.out.println("주문 데이터가 데이터 플랫폼으로 전송되었습니다: " + order);
    }
}
