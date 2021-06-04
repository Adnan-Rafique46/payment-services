package com.bundlen.paymentservices.service;

import com.bundlen.paymentservices.dto.CustomerDTO;
import com.bundlen.paymentservices.dto.request.PaymentOneTimeDTO;
import com.bundlen.paymentservices.dto.request.StripeSessionDTO;
import com.bundlen.paymentservices.dto.response.StripeSessionResponseDTO;
import org.springframework.scheduling.annotation.Scheduled;

public interface PaymentService {
    void paymentOneTime(PaymentOneTimeDTO dto);
    CustomerDTO createCustomer(StripeSessionDTO dto);

    CustomerDTO updateCustomerIntent(CustomerDTO dto);

    StripeSessionResponseDTO createStripeSession(CustomerDTO customerDTO);

    void chargeCustomers();
}
