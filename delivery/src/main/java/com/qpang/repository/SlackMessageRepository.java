package com.qpang.repository;

import com.qpang.domain.model.SlackMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SlackMessageRepository extends JpaRepository<SlackMessage, UUID> {

    List<SlackMessage> findAllByDeletedAtIsNullOrderByCreatedAtDesc();

}