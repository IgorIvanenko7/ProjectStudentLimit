package com.example.ProjectStudent.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class PaymentDto {

    private Long id;
    private Long idUser;
    private BigDecimal sumPay;
    private Instant datePay;

    public static PaymentDto createNewPayment(Long idUser, BigDecimal sumPay, Instant datePay) {
        PaymentDto payment = new PaymentDto();
        payment.setIdUser(idUser);
        payment.setSumPay(sumPay);
        payment.setDatePay(datePay);
        return payment;
    }
}
