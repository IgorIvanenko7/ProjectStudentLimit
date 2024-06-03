package com.example.ProjectStudent.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class LimitDto {

    private Long id;
    private Long idUser;
    private BigDecimal sumDaylimit;
    private Instant dateInstall;
    private BigDecimal sumBaselimit;
}
