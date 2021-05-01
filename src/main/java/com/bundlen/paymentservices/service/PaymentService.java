package com.bundlen.paymentservices.service;

import com.bundlen.paymentservices.dto.request.PaymentOneTimeDTO;

public interface PaymentService {
    void paymentOneTime(PaymentOneTimeDTO dto);
}
