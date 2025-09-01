package kr.hhplus.be.server.domain.order.vo;

import kr.hhplus.be.server.domain.coupon.vo.UserCouponVo;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.vo.CreateOrderProductUseCaseVo;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class CreateOrderVo {
    private final Long userId;
    private final List<CreateOrderProductVo> productList;
    private final Optional<CreateOrderCouponVo> coupon;
    private final Integer totalAmount;
    private final Integer finalAmount;

    private CreateOrderVo(Long userId, List<CreateOrderProductVo> productList, Optional<CreateOrderCouponVo> coupon, Integer totalPrice, Integer finalPrice) {
        this.userId = userId;
        this.productList = productList;
        this.coupon = coupon;
        this.totalAmount = totalPrice;
        this.finalAmount = finalPrice;
    }

    public static CreateOrderVo of(Long userId, List<Product> productList, List<CreateOrderProductUseCaseVo> productVoList, Optional<UserCouponVo> userCouponVo, Optional<Long> issuedCouponId) {
        List<CreateOrderProductVo> createOrderProductVoList = productVoList.stream()
                .map(productVo -> new CreateOrderProductVo(
                        productVo.getProductId(),
                        productList.stream()
                                .filter(product -> product.getId().equals(productVo.getProductId()))
                                .findFirst()
                                .map(Product::getPrice)
                                .orElse(0),
                        productVo.getQuantity()))
                .toList();
        Integer totalPrice = createOrderProductVoList.stream()
                .mapToInt(product -> product.getPrice() * product.getQuantity())
                .sum();
        Integer finalPrice = totalPrice;
        if (issuedCouponId.isPresent() && userCouponVo.isPresent()) {
            Integer discountAmount = userCouponVo.get().getDiscountAmount();
            finalPrice = totalPrice - discountAmount;
        }
        Optional<CreateOrderCouponVo> coupon = issuedCouponId.isPresent() && userCouponVo.isPresent()
                ? Optional.of(CreateOrderCouponVo.of(userCouponVo.get().getCouponId(), issuedCouponId.get(), userCouponVo.get().getDiscountAmount()))
                : Optional.empty();
        return new CreateOrderVo(userId, createOrderProductVoList, coupon, totalPrice, finalPrice);
    }
}
