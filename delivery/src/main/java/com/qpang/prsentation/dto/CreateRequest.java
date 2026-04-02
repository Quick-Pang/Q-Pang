package com.qpang.prsentation.dto;

import java.util.UUID;

public class CreateRequest {

    private UUID orderId;
    private UUID sourceHubId;
    private UUID destHubId;
    private String deliveryAddress;
    private String receiverName;
    private String receiverSlackId;
}
