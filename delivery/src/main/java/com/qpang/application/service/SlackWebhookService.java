package com.qpang.application.service;

import com.qpang.domain.model.SlackMessage;
import com.qpang.repository.SlackMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional
public class SlackWebhookService {

    private final SlackMessageRepository slackMessageRepository;

    @Value("${slack.webhook-url}")
    private String webhookUrl;

    public void sendSlackMessage(SlackMessage slackMessage) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String payload = """
                {
                  "text": "%s"
                }
                """.formatted(escapeJson(slackMessage.getMessage()));

        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    webhookUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                slackMessage.markSent();
            } else {
                slackMessage.markFailed("슬랙 발송 실패: " + response.getStatusCode());
            }

        } catch (Exception e) {
            slackMessage.markFailed(e.getMessage());
        }

        slackMessageRepository.save(slackMessage);
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}