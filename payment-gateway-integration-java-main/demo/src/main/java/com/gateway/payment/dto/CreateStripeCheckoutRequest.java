package com.gateway.payment.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateStripeCheckoutRequest(
        @NotNull UUID orderId
) {}
