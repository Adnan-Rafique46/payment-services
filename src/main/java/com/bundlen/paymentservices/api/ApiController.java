package com.bundlen.paymentservices.api;

import com.bundlen.paymentservices.dto.request.PaymentOneTimeDTO;
import com.bundlen.paymentservices.dto.request.StripeSessionDTO;
import com.bundlen.paymentservices.dto.response.BooleanResponseDTO;
import com.bundlen.paymentservices.dto.response.ResponseDTO;
import com.bundlen.paymentservices.dto.response.StripeSessionResponseDTO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spark.Request;
import spark.Response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ApiController {
    @RequestMapping(path="/payment-one-time", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BooleanResponseDTO> paymentOneTime(@RequestHeader("Authorization") String authorization,
                                                      @Validated @RequestBody PaymentOneTimeDTO dto);

    @RequestMapping(path="/create-session", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseDTO<StripeSessionResponseDTO>> createSession(@RequestHeader("Authorization") String authorization,
                                                                        @Validated @RequestBody StripeSessionDTO dto);

    @RequestMapping(path="/stripe-events", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object postEventsWebhook(@RequestBody String json, HttpServletRequest request, HttpServletResponse response);

    @RequestMapping(path="/checkout/success", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void checkoutSuccess(@PathVariable("session_id") String sessionId);
    @RequestMapping(path="/checkout/cancel", method= RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void checkoutCancel();
}
