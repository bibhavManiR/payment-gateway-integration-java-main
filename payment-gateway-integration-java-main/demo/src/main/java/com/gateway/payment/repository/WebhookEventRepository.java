package com.gateway.payment.repository;

import com.gateway.payment.entity.WebhookEventEntity;
import com.gateway.payment.entity.enums.PaymentProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookEventRepository extends JpaRepository<WebhookEventEntity, Long> {
    boolean existsByProviderAndEventId(PaymentProvider provider, String eventId);
}
