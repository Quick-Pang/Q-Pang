package com.qpang.orderservice.application;

import org.springframework.transaction.annotation.Transactional;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.orderservice.domain.OrderStatus;
import com.qpang.orderservice.domain.entity.Order;
import com.qpang.orderservice.domain.entity.OrderItem;
import com.qpang.orderservice.domain.repository.OrderRepository;
import com.qpang.orderservice.exception.OrderErrorCode;
import com.qpang.orderservice.infrastructure.client.ProductStockClient;
import com.qpang.orderservice.infrastructure.client.ProductStockFeignRequest;
import com.qpang.orderservice.presentation.dto.request.CreateOrderRequest;
import com.qpang.orderservice.presentation.dto.response.OrderResponse;
import com.qpang.orderservice.presentation.dto.response.OrderSummaryResponse;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductStockClient productStockClient;
    private static final List<Integer> ALLOWED_PAGE_SIZES = List.of(10, 30, 50);

    public Order createOrder(CreateOrderCommand command) {
        List<CreateOrderItemCommand> decreased = new ArrayList<>();
        try {
            for (var item : command.items()) {
                productStockClient.decreaseStock(
                        item.productId(),
                        new ProductStockFeignRequest(item.quantity()));
                decreased.add(item);
            }
            return orderRepository.save(command.toOrder());
        } catch (RuntimeException e) {
            restoreStockForItems(decreased, e);
            throw e;
        }
    }

    private void restoreStockForItems(List<CreateOrderItemCommand> decreased, RuntimeException cause) {
        for (int i = decreased.size() - 1; i >= 0; i--) {
            var item = decreased.get(i);
            try {
                productStockClient.increaseStock(
                        item.productId(),
                        new ProductStockFeignRequest(item.quantity()));
            } catch (RuntimeException restore) {
                cause.addSuppressed(restore);
            }
        }
    }

    public OrderResponse createOrderFromRequest(CreateOrderRequest req){
        CreateOrderCommand command = new CreateOrderCommand(
            req.supplyCompanyId(),
            req.requestCompanyId(),
            req.userId(),
            req.deliveryId(),
            req.price(),
            req.desiredArrival(),
            req.requestMemo(),
            req.createdBy(),
            req.items().stream().map(i -> new CreateOrderItemCommand(i.productId(), i.quantity())).toList()
        );


        return OrderResponse.from(createOrder(command));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderResponse(UUID orderId){
        return OrderResponse.from(getOrder(orderId));
    }

    private Pageable pageable(int page, int size, String sortBy, String sortDirection){
        int pageSize = ALLOWED_PAGE_SIZES.contains(size) ? size : 10;
        String property = "updatedAt".equalsIgnoreCase(sortDirection) ? "updatedAt" : "createdAt";
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;

        return PageRequest.of(page, pageSize, Sort.by(direction, property));
    }

    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getOrderSummaryList(int page, int size, String sortBy, String sortDirection){
        Pageable pageable = pageable(page, size, sortBy, sortDirection);
        return getOrderList(pageable).map(o->new OrderSummaryResponse(
            o.getId(), 
            o.getStatus(), 
            o.getPrice(), 
            o.getCreatedAt()));
    }

    @Transactional(readOnly = true)
    public Order getOrder(UUID orderId){
        Order order = getActiveOrder(orderId);
        order.getItems().size();
        return order;
    }

    @Transactional(readOnly = true)
    public Page<Order> getOrderList(Pageable pageable){
        return orderRepository.findAllByDeletedAtIsNull(pageable);
    }
    
    public void changeOrderStatus(UUID orderId, OrderStatus newStatus){
        if(newStatus == OrderStatus.CANCELLED){
            cancelOrder(orderId);
            return;
        }
        Order order = getActiveOrder(orderId);
        order.changeStatus(newStatus);
    }

    public void cancelOrder(UUID orderId) {
        Order order = getOrder(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new CustomException(OrderErrorCode.ORDER_ALREADY_CANCELED);
        }
        List<OrderItem> restored = new ArrayList<>();
        try {
            for (OrderItem line : order.getItems()) {
                productStockClient.increaseStock(
                        line.getProductId(),
                        new ProductStockFeignRequest(line.getQuantity()));
                restored.add(line);
            }
            order.changeStatus(OrderStatus.CANCELLED);
        } catch (RuntimeException e) {
            for (int i = restored.size() - 1; i >= 0; i--) {
                OrderItem line = restored.get(i);
                try {
                    productStockClient.decreaseStock(
                            line.getProductId(),
                            new ProductStockFeignRequest(line.getQuantity()));
                } catch (RuntimeException r) {
                    e.addSuppressed(r);
                }
            }
            throw e;
        }
    }

    public void deleteOrder(UUID orderId, UUID deletedBy){
        Order order = getActiveOrder(orderId);
        order.softDelete(deletedBy);
    }

    private Order getActiveOrder(UUID orderId){
        return orderRepository.findById(orderId).map(order->{
            if(order.getDeletedAt() != null){
                throw new CustomException(OrderErrorCode.ORDER_ALREADY_DELETED);
            }
            return order;
        }).orElseThrow(() 
                   -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
    }
}
