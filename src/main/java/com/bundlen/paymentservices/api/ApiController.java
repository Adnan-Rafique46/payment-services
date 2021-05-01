package com.bundlen.paymentservices.api;

import com.bundlen.paymentservices.dto.request.PaymentOneTimeDTO;
import com.bundlen.paymentservices.dto.response.BooleanResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public interface ApiController {
    @RequestMapping(path="/payment-one-time", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BooleanResponseDTO> paymentOneTime(@RequestHeader("Authorization") String authorization,
                                                      @Validated @RequestBody PaymentOneTimeDTO dto);

    /*@RequestMapping(path="/payment-subscription", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BooleanResponseDTO> paymentSubscription(@RequestHeader("Authorization") String authorization,
                                                           @Validated @RequestBody NotificationDTO dto);*/
}
