package com.bundlen.paymentservices.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)
@Document("Billing")
public class Billing {
    @Id
    private String id;
    private String stripeId;
    private Double amount;
    private String currency;
    private String description;
}
