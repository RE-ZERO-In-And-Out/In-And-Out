package com.rezero.inandout.exception;

import com.rezero.inandout.exception.response.IncomeErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(IncomeException.class)
    protected ResponseEntity<IncomeErrorResponse> incomeHandlerCustomException(IncomeException e) {
        IncomeErrorResponse errorResponse = IncomeErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .message(e.getErrorCode().getDescription())
            .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }




}
