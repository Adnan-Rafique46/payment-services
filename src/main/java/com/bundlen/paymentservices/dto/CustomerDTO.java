package com.bundlen.paymentservices.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)
public class CustomerDTO {
    private String id;
    private String stripeCustomerId;
    private String organizationId;
    private String stripeIntentId;
    private String stripePaymentMethod;
    private Date checkoutDate;
    private Date nextPaymentDate;
}
