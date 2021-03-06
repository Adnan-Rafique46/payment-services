package com.bundlen.paymentservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StripeSessionResponseDTO {
    private String stripeSessionId;
    private String stripeCustomerId;
}
