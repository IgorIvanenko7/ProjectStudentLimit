package com.example.ProjectStudent.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionDto {

    private final int code;
    private final String message;
}
