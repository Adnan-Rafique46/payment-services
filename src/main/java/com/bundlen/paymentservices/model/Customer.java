package com.bundlen.paymentservices.model;

import com.bundlen.paymentservices.dto.CustomerDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

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
    private String stripeIntentId;
    private String stripePaymentMethod;
    private Date checkoutDate;
    private Date nextPaymentDate;

    public CustomerDTO toDTO() {
        return CustomerDTO.builder().id(this.getId()).stripeCustomerId(this.getStripeCustomerId())
                .organizationId(this.getOrganizationId()).stripeIntentId(this.stripeIntentId).stripePaymentMethod(this.stripePaymentMethod)
                .checkoutDate(this.checkoutDate).nextPaymentDate(this.nextPaymentDate).build();
    }
}
