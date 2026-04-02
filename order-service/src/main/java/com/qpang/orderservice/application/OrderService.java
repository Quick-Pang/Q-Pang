package com.qpang.orderservice.application;

import org.springframework.transaction.annotation.Transactional;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.orderservice.domain.OrderStatus;
import com.qpang.orderservice.domain.entity.Order;
import com.qpang.orderservice.domain.repository.OrderRepository;
import com.qpang.orderservice.presentation.dto.request.CreateOrderRequest;
import com.qpang.orderservice.presentation.dto.response.OrderResponse;
import com.qpang.orderservice.presentation.dto.response.OrderSummaryResponse;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Transactional
@RequiredArgsConstructor
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public Order createdOrder(CreateOrderCommand command){
        Order order =Order.create(
            command.supplyCompanyId(),
            command.requestCompanyId(),
            command.userId(),
            command.deliveryId(),
            command.price(),
            command.desiredArrival(),
            command.requestMemo(),
            command.createdBy()
        );
        for(CreateOrderItemCommand itemCommand : command.items()){
            order.addItem(itemCommand.productId(),itemCommand.quantity(),command.createdBy());
        }
        return orderRepository.save(order);
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


        return OrderResponse.from(createdOrder(command));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderResponse(UUID orderId){
        return OrderResponse.from(getOrder(orderId));
    }

    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getOrderSummaryList(Pageable pageable){
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
        Order order = getActiveOrder(orderId);
        order.changeStatus(newStatus);
    }

    public void deleteOrder(UUID orderId, UUID deletedBy){
        Order order = getActiveOrder(orderId);
        order.softDelete(deletedBy);
    }

    private Order getActiveOrder(UUID orderId){
        return orderRepository.findByIdAndDeletedAtIsNull(orderId).orElseThrow(() -> new CustomException(CommonErrorCode.NOT_FOUND));
    }
}
