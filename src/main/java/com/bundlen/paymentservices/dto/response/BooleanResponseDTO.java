package com.bundlen.paymentservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BooleanResponseDTO extends BaseResponseDTO {
  Boolean result;
}
