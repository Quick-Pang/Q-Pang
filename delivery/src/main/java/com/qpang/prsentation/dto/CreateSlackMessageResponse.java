package com.qpang.prsentation.dto;

import com.qpang.domain.enums.SlackMessageStatus;
import com.qpang.domain.model.SlackMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateSlackMessageResponse {

    private UUID id;
    private SlackMessageStatus status;

    public static CreateSlackMessageResponse from(SlackMessage slackMessage) {
        return new CreateSlackMessageResponse(
                slackMessage.getId(),
                slackMessage.getStatus()
        );
    }
}