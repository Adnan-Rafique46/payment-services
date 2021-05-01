package com.bundlen.paymentservices.service;

import com.bundlen.paymentservices.dto.request.PaymentOneTimeDTO;
import com.bundlen.paymentservices.model.Billing;
import com.bundlen.paymentservices.repository.BillingRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl {
    @Autowired
    private BillingRepository repository;

    public void paymentOneTime(PaymentOneTimeDTO dto) {
        String stripeId = null;
        double amount = dto.getAmount()*100;
        try {
            Map chargeParams = new HashMap<>();
            chargeParams.put("amount", amount);
            chargeParams.put("currency", dto.getCurrency());
            chargeParams.put("description", dto.getDescription());
            chargeParams.put("source", dto.getSource());
            Charge charge = Charge.create(chargeParams);
            stripeId = charge.getId();
        }
        catch(StripeException e) {
            throw new RuntimeException("Unable to process the charge", e);
        }
        repository.save(Billing.builder().stripeId(stripeId).amount(amount).currency(dto.getCurrency())
                .description(dto.getDescription()).build());
    }
}
