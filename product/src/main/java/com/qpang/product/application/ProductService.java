package com.qpang.product.application;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.product.client.CompanyClient;
import com.qpang.product.domain.entity.Product;
import com.qpang.product.domain.enums.ProductStatus;
import com.qpang.product.exception.ProductErrorCode;
import com.qpang.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyClient companyClient;

    public Product create(String name, UUID companyId, UUID hubId) {

        // ✅ 업체 존재 여부 확인
        try {
            companyClient.getCompany(companyId);
        } catch (Exception e) {
            throw new CustomException(ProductErrorCode.COMPANY_NOT_FOUND);
        }

        return productRepository.save(
                Product.builder()
                        .name(name)
                        .companyId(companyId)
                        .hubId(hubId)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public Product findById(UUID id) {
        return productRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<Product> search(UUID companyId, String name, int page, int size, String sortBy) {
        size = List.of(10, 30, 50).contains(size) ? size : 10;
        Sort sort = Sort.by(Sort.Direction.DESC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        if (companyId != null) {
            return productRepository.findByCompanyIdAndNameContainingAndDeletedAtIsNull(companyId, name, pageable);
        }
        return productRepository.findByNameContainingAndDeletedAtIsNull(name, pageable);
    }

    public void update(UUID id, String name) {
        if (name == null || name.isBlank()) {
            throw new CustomException(CommonErrorCode.MISSING_INPUT_VALUE);
        }
        Product product = findById(id);
        product.update(name);
    }

    public void changeStatus(UUID id, ProductStatus status) {
        if (status == null) {
            throw new CustomException(CommonErrorCode.MISSING_INPUT_VALUE);
        }
        Product product = findById(id);
        product.changeStatus(status);
    }

    public void increaseStock(UUID id, int quantity) {
        if (quantity < 1) {
            throw new CustomException(ProductErrorCode.INVALID_STOCK_QUANTITY);
        }
        Product product = findById(id);
        if (product.getDeletedAt() != null) {
            throw new CustomException(ProductErrorCode.PRODUCT_ALREADY_DELETED);
        }
        product.increaseStock(quantity);
    }

    public void decreaseStock(UUID id, int quantity) {
        if (quantity < 1) {
            throw new CustomException(ProductErrorCode.INVALID_STOCK_QUANTITY);
        }
        Product product = findById(id);
        if (product.getDeletedAt() != null) {
            throw new CustomException(ProductErrorCode.PRODUCT_ALREADY_DELETED);
        }
        product.decreaseStock(quantity);
    }

    public void delete(UUID id, Long userId) {
        Product product = findById(id);
        if (product.getDeletedAt() != null) {
            throw new CustomException(ProductErrorCode.PRODUCT_ALREADY_DELETED);
        }
        product.delete(userId);
    }
}