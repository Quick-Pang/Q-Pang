package com.qpang.orderservice.application;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.common.response.APIResponse;
import com.qpang.orderservice.exception.OrderErrorCode;
import com.qpang.orderservice.infrastructure.client.ProductQueryClient;
import com.qpang.orderservice.infrastructure.client.ProductQueryClient.ProductHubResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HubResolver {

    private final ProductQueryClient productQueryClient;

    /**
     * 주문 라인의 상품들로부터 출발 허브를 정한다.
     * 규칙: 모든 상품의 hubId가 같아야 함 (다르면 예외 — 팀에서 분할 배송 등으로 변경 가능).
     */
    public UUID resolveFromItems(List<CreateOrderItemCommand> items) {
        if (items == null || items.isEmpty()) {
            throw new CustomException(OrderErrorCode.ORDER_ITEM_INVALID);
        }
        UUID hub = hubOfProduct(items.get(0).productId());
        for (int i = 1; i < items.size(); i++) {
            UUID next = hubOfProduct(items.get(i).productId());
            if (!hub.equals(next)) {
                throw new CustomException(OrderErrorCode.ORDER_ITEM_INVALID);
            }
        }
        return hub;
    }

    private UUID hubOfProduct(UUID productId) {
        APIResponse<ProductHubResponse> res = productQueryClient.getProduct(productId);
        if (res == null || !res.isSuccess() || res.getData() == null || res.getData().hubId() == null) {
            throw new CustomException(CommonErrorCode.NOT_FOUND);
        }
        return res.getData().hubId();
    }
}
