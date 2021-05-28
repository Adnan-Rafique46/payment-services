package com.bundlen.paymentservices.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StripeSessionDTO {
    @NotEmpty(message = "Organization can't be empty")
    @NotNull(message = "Organization can't be null")
    private String organizationId;
    @NotEmpty(message = "Email can't be empty")
    @NotNull(message = "Email can't be null")
    private String email;
    @NotEmpty(message = "Token can't be empty")
    @NotNull(message = "Token can't be null")
    private String token;
}
