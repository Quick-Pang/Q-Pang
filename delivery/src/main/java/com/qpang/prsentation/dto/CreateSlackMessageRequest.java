package com.qpang.prsentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateSlackMessageRequest {

    private String receiverSlackId;
    private String message;
    private UUID senderUserId;
    private String relatedType;
    private UUID relatedId;
}