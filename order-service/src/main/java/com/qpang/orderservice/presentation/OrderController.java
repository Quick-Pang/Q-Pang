package com.qpang.orderservice.presentation;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.qpang.orderservice.application.OrderService;
import com.qpang.orderservice.presentation.dto.request.ChangeOrderStatusRequest;
import com.qpang.orderservice.presentation.dto.request.CreateOrderRequest;
import com.qpang.orderservice.presentation.dto.response.OrderResponse;
import com.qpang.orderservice.presentation.dto.response.OrderSummaryResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(
            @RequestBody @Valid CreateOrderRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        return orderService.createOrderFromRequest(request, userId);
    }

    @GetMapping("/{orderId}")
    public OrderResponse get(@PathVariable UUID orderId){
        return orderService.getOrderResponse(orderId);
    }

    @GetMapping
    public Page<OrderSummaryResponse> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "DESC") String sortDirection){
        return orderService.getOrderSummaryList(page, size, sortBy, sortDirection);
    }

    @PatchMapping("/{orderId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchStatus(@PathVariable UUID orderId, @RequestBody @Valid ChangeOrderStatusRequest request){
        orderService.changeOrderStatus(orderId, request.status());
    }
    
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{orderId}/cancel")
    public void cancel(@PathVariable UUID orderId){
        orderService.cancelOrder(orderId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderId}")
    public void delete(
            @PathVariable UUID orderId,
            @RequestHeader("X-User-Id") UUID deletedBy) {
        orderService.deleteOrder(orderId, deletedBy);
    }


}