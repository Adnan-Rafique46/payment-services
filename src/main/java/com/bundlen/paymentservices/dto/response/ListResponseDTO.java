package com.bundlen.paymentservices.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListResponseDTO<T> extends BaseResponseDTO {
    List<T> item;
}
