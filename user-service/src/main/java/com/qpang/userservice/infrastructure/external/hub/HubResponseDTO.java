package com.qpang.userservice.infrastructure.external.hub;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class HubResponseDTO {
    private UUID id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
}
