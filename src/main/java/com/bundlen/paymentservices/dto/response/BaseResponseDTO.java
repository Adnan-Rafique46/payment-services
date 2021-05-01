package com.bundlen.paymentservices.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public abstract class BaseResponseDTO implements Serializable {
    ErrorDTO errorDTO;
}
