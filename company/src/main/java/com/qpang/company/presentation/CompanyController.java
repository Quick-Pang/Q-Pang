package com.qpang.company.presentation;
import com.qpang.company.application.CompanyService;
import com.qpang.company.domain.entity.Company;
import com.qpang.company.dto.CompanyCreateRequest;
import com.qpang.company.dto.CompanyResponse;
import com.qpang.company.dto.CompanyStatusUpdateRequest;
import com.qpang.company.dto.CompanyUpdateRequest;
import lombok.RequiredArgsConstructor;
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
    public CompanyResponse create(@RequestBody CompanyCreateRequest request) {
        Company company = companyService.create(
                request.getName(),
                request.getType(),
                request.getHubId(),
                request.getAddress(),
                request.getManagerUserId()
        );

        return CompanyResponse.from(company);
    }

    // 전체 조회
    @GetMapping
    public List<CompanyResponse> findAll() {
        return companyService.findAll()
                .stream()
                .map(CompanyResponse::from)
                .toList();
    }

    // 허브별 조회
    /*@GetMapping("/hub/{hubId}")
    public List<CompanyResponse> findByHub(@PathVariable UUID hubId) {
        return companyService.findByHub(hubId)
                .stream()
                .map(CompanyResponse::from)
                .toList();
    }
*/

    //단건 조회
    @GetMapping("/{id}")
    public CompanyResponse findById(@PathVariable UUID id) {
        return CompanyResponse.from(companyService.findById(id));
    }

    // 수정
    @PatchMapping("/{id}")
    public void update(@PathVariable UUID id,
                       @RequestBody CompanyUpdateRequest request) {

        companyService.update(id, request.getName(), request.getAddress());
    }

    // 상태 변경
    @PatchMapping("/{id}/status")
    public void changeStatus(@PathVariable UUID id,
                             @RequestBody CompanyStatusUpdateRequest request) {

        companyService.changeStatus(id, request.getStatus());
    }
    //삭제 (soft delete)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id,
                       @RequestParam Long userId) {

        companyService.delete(id, userId);
    }
}