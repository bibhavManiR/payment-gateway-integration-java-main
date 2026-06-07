package com.gateway.payment.dto;

import com.gateway.payment.entity.enums.PaymentStatus;

public record PaymentStatusUpdateRequest(
        PaymentStatus paymentStatus,
        String paymentReferenceId
) {}
