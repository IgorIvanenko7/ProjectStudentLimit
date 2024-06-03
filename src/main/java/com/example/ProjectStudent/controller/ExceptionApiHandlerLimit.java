package com.example.ProjectStudent.controller;

import com.example.ProjectStudent.dto.ExceptionDto;
import com.example.ProjectStudent.handleExeption.HandlerExeptionLimit;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionApiHandlerLimit {

    @ExceptionHandler(HandlerExeptionLimit.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionDto customException(HandlerExeptionLimit exception) {
        return ExceptionDto.builder().code(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage()).build();
    }
}
