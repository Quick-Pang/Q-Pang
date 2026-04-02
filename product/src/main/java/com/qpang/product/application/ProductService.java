package com.qpang.product.application;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.product.domain.entity.Product;
import com.qpang.product.domain.enums.ProductStatus;
import com.qpang.product.exception.ProductErrorCode;
import com.qpang.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public Product create(String name, UUID companyId, UUID hubId) {
        return productRepository.save(
                Product.builder()
                        .name(name)
                        .companyId(companyId)
                        .hubId(hubId)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAllByDeletedAtIsNull();
    }

    @Transactional(readOnly = true)
    public List<Product> findAllByCompanyId(UUID companyId) {
        return productRepository.findAllByCompanyIdAndDeletedAtIsNull(companyId);
    }

    @Transactional(readOnly = true)
    public Product findById(UUID id) {
        return productRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    public void update(UUID id, String name) {
        if (name == null || name.isBlank()) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
        Product product = findById(id);
        product.update(name);
    }

    public void changeStatus(UUID id, ProductStatus status) {
        if (status == null) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
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