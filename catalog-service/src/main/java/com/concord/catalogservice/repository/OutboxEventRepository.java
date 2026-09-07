package com.concord.catalogservice.repository;

import com.concord.catalogservice.entity.OutboxEvent;
import com.concord.catalogservice.entity.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}