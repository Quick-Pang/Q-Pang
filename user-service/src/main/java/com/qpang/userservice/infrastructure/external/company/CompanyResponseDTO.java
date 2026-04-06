package com.qpang.userservice.infrastructure.external.company;

import java.util.UUID;

public record CompanyResponseDTO(
        UUID id,
        String type,
        UUID hubId,
        String address,
        UUID managerUserId,
        String status
) {
}
