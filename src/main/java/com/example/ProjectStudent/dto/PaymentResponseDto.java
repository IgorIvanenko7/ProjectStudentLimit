package com.example.ProjectStudent.dto;

import lombok.Data;

@Data
public class PaymentResponseDto {

    private PaymentDto paymentDto;
    private LimitDto limitDto;

    public static PaymentResponseDto createPaymentResponseDto(PaymentDto paymentDto, LimitDto limitDto) {
        PaymentResponseDto payment = new PaymentResponseDto();
        payment.setPaymentDto(paymentDto);
        payment.setLimitDto(limitDto);
        return payment;
    }
}
