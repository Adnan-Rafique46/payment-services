package com.bundlen.paymentservices.service;

import com.bundlen.paymentservices.dto.CustomerDTO;
import com.bundlen.paymentservices.dto.request.PaymentOneTimeDTO;
import com.bundlen.paymentservices.dto.request.StripeSessionDTO;
import com.bundlen.paymentservices.dto.response.StripeSessionResponseDTO;

public interface PaymentService {
    void paymentOneTime(PaymentOneTimeDTO dto);
    CustomerDTO createCustomer(StripeSessionDTO dto);
    StripeSessionResponseDTO createStripeSession(CustomerDTO customerDTO);
}
