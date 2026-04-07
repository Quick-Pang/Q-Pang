package com.qpang.product.presentation;

import com.qpang.common.response.APIResponse;
import com.qpang.product.application.ProductService;
import com.qpang.product.domain.entity.Product;
import com.qpang.product.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    // 상품 생성
    @PostMapping
    public ResponseEntity<APIResponse<ProductResponse>> create(@Valid @RequestBody ProductCreateRequest request) {
        Product product = productService.create(
                CreateProductCommand.builder()
                        .name(request.getName())
                        .companyId(request.getCompanyId())
                        .hubId(request.getHubId())
                        .build()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success(ProductResponse.from(product)));
    }

    // 전체/업체별 조회
    @GetMapping
    public ResponseEntity<APIResponse<Page<ProductResponse>>> findAll(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        Page<ProductResponse> responses = productService.search(companyId, name, page, size, sortBy)
                .map(ProductResponse::from);

        return ResponseEntity.ok(APIResponse.success(responses));
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<ProductResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(APIResponse.success(ProductResponse.from(productService.findById(id))));
    }

    // 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id,
                                       @Valid @RequestBody ProductUpdateRequest request) {
        productService.update(id, request.getName());
        return ResponseEntity.noContent().build();
    }

    // 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable UUID id,
                                             @Valid @RequestBody ProductStatusUpdateRequest request) {
        productService.changeStatus(id, request.getStatus());
        return ResponseEntity.noContent().build();
    }

    // 재고 증가
    @PostMapping("/{id}/stock/increase")
    public ResponseEntity<Void> increaseStock(@PathVariable UUID id,
                                              @Valid @RequestBody ProductStockRequest request) {
        productService.increaseStock(id, request.getQuantity());
        return ResponseEntity.noContent().build();
    }

    // 재고 차감
    @PostMapping("/{id}/stock/decrease")
    public ResponseEntity<Void> decreaseStock(@PathVariable UUID id,
                                              @Valid @RequestBody ProductStockRequest request) {
        productService.decreaseStock(id, request.getQuantity());
        return ResponseEntity.noContent().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       @RequestParam UUID userId) {
        productService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}