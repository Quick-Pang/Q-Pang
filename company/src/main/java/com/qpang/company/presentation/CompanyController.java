package com.qpang.company.presentation;
import com.qpang.common.response.APIResponse;
import com.qpang.company.application.CompanyService;
import com.qpang.company.domain.entity.Company;
import com.qpang.company.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
public class CompanyController {

    private final CompanyService companyService;

    // 업체 생성
    @PostMapping
    public ResponseEntity<APIResponse<CompanyResponse>> create(@Valid @RequestBody CompanyCreateRequest request) {
        Company company = companyService.create(
                CreateCompanyCommand.builder()
                        .name(request.getName())
                        .type(request.getType())
                        .hubId(request.getHubId())
                        .address(request.getAddress())
                        .managerUserId(request.getManagerUserId())
                        .build()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success(CompanyResponse.from(company)));
    }

    // 전체 조회 및 허브 별 조회
    @GetMapping
    public ResponseEntity<APIResponse<Page<CompanyResponse>>> findAll(
            @RequestParam(required = false) UUID hubId,
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        Page<CompanyResponse> responses = companyService.search(hubId, name, page, size, sortBy)
                .map(CompanyResponse::from);

        return ResponseEntity.ok(APIResponse.success(responses));
    }


    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<CompanyResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(APIResponse.success(CompanyResponse.from(companyService.findById(id))));
    }

    // 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable UUID id,
                                       @Valid @RequestBody CompanyUpdateRequest request) {
        companyService.update(id, request.getName(), request.getAddress());
        return ResponseEntity.noContent().build();
    }

    // 상태 변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable UUID id,
                                             @Valid @RequestBody CompanyStatusUpdateRequest request) {
        companyService.changeStatus(id, request.getStatus());
        return ResponseEntity.noContent().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       @RequestParam Long userId) {
        //companyService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}