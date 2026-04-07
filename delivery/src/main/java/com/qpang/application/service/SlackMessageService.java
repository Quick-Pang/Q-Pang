package com.qpang.application.service;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.domain.model.SlackMessage;
import com.qpang.prsentation.dto.CreateSlackMessageRequest;
import com.qpang.prsentation.dto.CreateSlackMessageResponse;
import com.qpang.repository.SlackMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SlackMessageService {

    private final SlackMessageRepository slackMessageRepository;
    private final SlackWebhookService slackWebhookService;

    public CreateSlackMessageResponse createSlackMessage(CreateSlackMessageRequest request) {
        validateCreateSlackMessageRequest(request);

        SlackMessage slackMessage = SlackMessage.create(
                request.getReceiverSlackId(),
                request.getMessage(),
                request.getSenderUserId(),
                request.getRelatedType(),
                request.getRelatedId()
        );

        SlackMessage savedSlackMessage = slackMessageRepository.save(slackMessage);
        return CreateSlackMessageResponse.from(savedSlackMessage);
    }

    public CreateSlackMessageResponse createAndSendSlackMessage(CreateSlackMessageRequest request) {
        validateCreateSlackMessageRequest(request);

        SlackMessage slackMessage = SlackMessage.create(
                request.getReceiverSlackId(),
                request.getMessage(),
                request.getSenderUserId(),
                request.getRelatedType(),
                request.getRelatedId()
        );

        SlackMessage savedSlackMessage = slackMessageRepository.save(slackMessage);

        slackWebhookService.sendSlackMessage(savedSlackMessage);

        return CreateSlackMessageResponse.from(savedSlackMessage);
    }

    private void validateCreateSlackMessageRequest(CreateSlackMessageRequest request) {
        if (request == null ||
                request.getReceiverSlackId() == null || request.getReceiverSlackId().trim().isEmpty() ||
                request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
    }
}