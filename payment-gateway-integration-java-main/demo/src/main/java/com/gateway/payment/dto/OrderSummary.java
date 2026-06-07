package com.gateway.payment.dto;


import java.util.UUID;

public record OrderSummary(
        UUID id,
        long totalAmount,
        String currency
) {}

