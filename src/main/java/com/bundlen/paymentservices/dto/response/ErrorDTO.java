package com.bundlen.paymentservices.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDTO {
    String message;
    String rootCause;
    String code;
    String name;
    String source;
    String requestURI;
    Exception ex;
}
