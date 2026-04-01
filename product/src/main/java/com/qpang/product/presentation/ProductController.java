package com.qpang.product.presentation;

import com.qpang.product.application.ProductService;
import com.qpang.product.domain.entity.Product;
import com.qpang.product.dto.*;
import lombok.RequiredArgsConstructor;
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
    public ProductResponse create(@Valid @RequestBody ProductCreateRequest request) {
        Product product = productService.create(
                request.getName(),
                request.getCompanyId(),
                request.getHubId()
        );
        return ProductResponse.from(product);
    }

    // 전체 상품 조회 / 업체별 상품 조회
    @GetMapping
    public List<ProductResponse> findAll(@RequestParam(required = false) UUID companyId) {
        if (companyId != null) {
            return productService.findAllByCompanyId(companyId)
                    .stream()
                    .map(ProductResponse::from)
                    .toList();
        }
        return productService.findAll()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable UUID id) {
        return ProductResponse.from(productService.findById(id));
    }

    // 상품 수정
    @PatchMapping("/{id}")
    public void update(@PathVariable UUID id,
                       @Valid @RequestBody ProductUpdateRequest request) {
        productService.update(id, request.getName());
    }

    // 상태 변경
    @PatchMapping("/{id}/status")
    public void changeStatus(@PathVariable UUID id,
                             @Valid @RequestBody ProductStatusUpdateRequest request) {
        productService.changeStatus(id, request.getStatus());
    }

    // 재고 증가
    @PostMapping("/{id}/stock/increase")
    public void increaseStock(@PathVariable UUID id,
                              @Valid @RequestBody ProductStockRequest request) {
        productService.increaseStock(id, request.getQuantity());
    }

    // 재고 차감
    @PostMapping("/{id}/stock/decrease")
    public void decreaseStock(@PathVariable UUID id,
                              @Valid @RequestBody ProductStockRequest request) {
        productService.decreaseStock(id, request.getQuantity());
    }

    // 삭제 (soft delete)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id,
                       @RequestParam Long userId) {
        productService.delete(id, userId);
    }
}