package com.bundlen.paymentservices.api;

import com.bundlen.paymentservices.dto.request.PaymentOneTimeDTO;
import com.bundlen.paymentservices.dto.response.BooleanResponseDTO;
import com.bundlen.paymentservices.service.PaymentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/controller")
public class ApiControllerImpl implements ApiController {
    @Autowired
    private PaymentServiceImpl paymentService;

    @Override
    public ResponseEntity<BooleanResponseDTO> paymentOneTime(@RequestHeader("Authorization") String authorization,
                                                             @Validated @RequestBody PaymentOneTimeDTO dto) {
        paymentService.paymentOneTime(dto);
        return ResponseEntity.status(CREATED).body(new BooleanResponseDTO(true));
    }
}
