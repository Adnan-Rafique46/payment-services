package com.bundlen.paymentservices.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOneTimeDTO {
    @NotNull(message = "Amount can't be null")
    private Double amount;
    @NotEmpty(message = "Currency can't be empty")
    @NotNull(message = "Currency can't be null")
    private String currency;
    @NotEmpty(message = "Description can't be empty")
    @NotNull(message = "Description can't be null")
    private String description;
    @NotEmpty(message = "Source can't be empty")
    @NotNull(message = "Source can't be null")
    private String source;
}
