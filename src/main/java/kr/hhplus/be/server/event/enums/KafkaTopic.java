package kr.hhplus.be.server.event.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KafkaTopic {
    ORDER_REQUEST(KafkaTopic.name.ORDER_REQUEST),
    ORDER_COMPLETE(KafkaTopic.name.ORDER_COMPLETE),
    USE_BALANCE_COMPLETE(KafkaTopic.name.USE_BALANCE_COMPLETE),
    DECREASE_STOCK_COMPLETE(KafkaTopic.name.DECREASE_STOCK_COMPLETE),
    USE_COUPON_COMPLETE(KafkaTopic.name.USE_COUPON_COMPLETE),
    CREATE_ORDER_DATA_COMPLETE(KafkaTopic.name.CREATE_ORDER_DATA_COMPLETE),

    ORDER_REQUEST_COMPENSATION(KafkaTopic.name.ORDER_REQUEST_COMPENSATION),
    ORDER_COMPLETE_COMPENSATION(KafkaTopic.name.ORDER_COMPLETE_COMPENSATION),
    USE_BALANCE_COMPENSATION(KafkaTopic.name.USE_BALANCE_COMPENSATION),
    DECREASE_STOCK_COMPENSATION(KafkaTopic.name.DECREASE_STOCK_COMPENSATION),
    USE_COUPON_COMPENSATION(KafkaTopic.name.USE_COUPON_COMPENSATION),
    CREATE_ORDER_DATA_COMPENSATION(KafkaTopic.name.CREATE_ORDER_DATA_COMPENSATION),

    DEFAULT(KafkaTopic.name.DEFAULT)
    ;

    private final String topicName;

    public static class name {
        public static final String ORDER_REQUEST = "order_request";
        public static final String ORDER_COMPLETE = "order_complete";
        public static final String USE_BALANCE_COMPLETE = "use_balance_complete";
        public static final String DECREASE_STOCK_COMPLETE = "decrease_stock_complete";
        public static final String USE_COUPON_COMPLETE = "use_coupon_complete";
        public static final String CREATE_ORDER_DATA_COMPLETE = "create_order_data_complete";

        public static final String ORDER_REQUEST_COMPENSATION = "order-request-compensation";
        public static final String ORDER_COMPLETE_COMPENSATION = "order-compensation";
        public static final String USE_BALANCE_COMPENSATION = "use-balance-compensation";
        public static final String DECREASE_STOCK_COMPENSATION = "decrease-stock-compensation";
        public static final String USE_COUPON_COMPENSATION = "use-coupon-compensation";
        public static final String CREATE_ORDER_DATA_COMPENSATION = "create-order-data-compensation";

        public static final String DEFAULT = "default";
    }

    public static class groupId {
        public static final String ORDER_COMPLETE = "order-complete-group";
        public static final String USE_BALANCE = "use-balance-group";
        public static final String DECREASE_STOCK = "decrease-stock-group";
        public static final String USE_COUPON = "use-coupon-group";
        public static final String SEND_DATA_PLATFORM = "send-data-platform-group";

        public static final String DEFAULT = "default-group";
    }
}
