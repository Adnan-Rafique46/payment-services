package com.bundlen.paymentservices.model;

import com.bundlen.paymentservices.dto.CustomerDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)
@Document("Customer")
public class Customer {
    @Id
    private String id;
    private String stripeCustomerId;
    private String organizationId;

    public CustomerDTO toDTO() {
        return CustomerDTO.builder().id(this.getId()).stripeCustomerId(this.getStripeCustomerId())
                .organizationId(this.getOrganizationId()).build();
    }
}
