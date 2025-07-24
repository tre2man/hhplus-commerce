package kr.hhplus.be.server.domain.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "쿠폰 정보 조회")
public class GetCouponResponse {
    @Schema(description = "쿠폰 ID", example = "1")
    private Long id;

    @Schema(description = "쿠폰 이름", example = "할인 쿠폰")
    private String name;

    @Schema(description = "쿠폰 설명", example = "10% 할인 쿠폰")
    private String description;

    @Schema(description = "쿠폰 할인 금액", example = "1000")
    private Long discountAmount;

    @Schema(description = "쿠폰 발급 날짜", example = "2023-09-01")
    private String issueDate;

    public GetCouponResponse(Long id, String name, String description, Long discountAmount, String issueDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.discountAmount = discountAmount;
        this.issueDate = issueDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getDiscountAmount() {
        return discountAmount;
    }

    public String getIssueDate() {
        return issueDate;
    }
}
