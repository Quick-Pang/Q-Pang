package com.qpang.domain.model;

import com.qpang.common.entity.BaseUserEntity;
import com.qpang.domain.enums.SlackMessageStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_slack")
@Getter
@NoArgsConstructor
public class SlackMessage extends BaseUserEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "receiver_slack_id")
    private String receiverSlackId;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "sender_user_id")
    private UUID senderUserId;

    @Column(name = "related_type")
    private String relatedType;

    @Column(name = "related_id")
    private UUID relatedId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SlackMessageStatus status;

    @Column(name = "fail_reason", columnDefinition = "TEXT")
    private String failReason;

    @Column(name = "retry_count")
    private Integer retryCount;

    private SlackMessage(
            String receiverSlackId,
            String message,
            UUID senderUserId,
            String relatedType,
            UUID relatedId
    ) {
        this.receiverSlackId = receiverSlackId;
        this.message = message;
        this.senderUserId = senderUserId;
        this.relatedType = relatedType;
        this.relatedId = relatedId;
        this.status = SlackMessageStatus.PENDING;
        this.retryCount = 0;
    }

    public static SlackMessage create(
            String receiverSlackId,
            String message,
            UUID senderUserId,
            String relatedType,
            UUID relatedId
    ) {
        return new SlackMessage(
                receiverSlackId,
                message,
                senderUserId,
                relatedType,
                relatedId
        );
    }

    public void markSent() {
        this.status = SlackMessageStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void markFailed(String failReason) {
        this.status = SlackMessageStatus.FAILED;
        this.failReason = failReason;
        this.retryCount = this.retryCount + 1;
    }

    public void delete(UUID deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}