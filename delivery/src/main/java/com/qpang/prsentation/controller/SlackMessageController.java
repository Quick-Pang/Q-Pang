package com.qpang.prsentation.controller;

import com.qpang.application.service.SlackMessageService;
import com.qpang.prsentation.dto.CreateSlackMessageRequest;
import com.qpang.prsentation.dto.CreateSlackMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class SlackMessageController {

    private final SlackMessageService slackMessageService;

    /**
     * 슬랙 메시지 저장만 (DB 기록용)
     */
    @PostMapping
    public CreateSlackMessageResponse createSlackMessage(
            @RequestBody CreateSlackMessageRequest request
    ) {
        return slackMessageService.createSlackMessage(request);
    }

    /**
     * 슬랙 메시지 저장 + 실제 발송
     */
    @PostMapping("/send")
    public CreateSlackMessageResponse createAndSendSlackMessage(
            @RequestBody CreateSlackMessageRequest request
    ) {
        return slackMessageService.createAndSendSlackMessage(request);
    }
}