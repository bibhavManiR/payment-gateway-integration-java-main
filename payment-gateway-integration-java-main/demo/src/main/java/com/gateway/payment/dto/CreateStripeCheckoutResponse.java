package com.gateway.payment.dto;

public record CreateStripeCheckoutResponse(
        String checkoutUrl
) {}
