package com.rezero.inandout.exception;

import com.rezero.inandout.exception.response.*;
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

    @ExceptionHandler(ExpenseException.class)
    protected ResponseEntity<ExpenseErrorResponse> incomeHandlerCustomException(
        ExpenseException e) {
        ExpenseErrorResponse errorResponse = ExpenseErrorResponse.builder()
            .errorCode(e.getErrorCode())
            .message(e.getErrorCode().getDescription())
            .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DiaryException.class)
    protected ResponseEntity<DiaryErrorResponse> MemberHandlerCustomException(
            DiaryException e) {
        DiaryErrorResponse errorResponse = DiaryErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .message(e.getErrorCode().getDescription())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MemberException.class)
    protected ResponseEntity<MemberErrorResponse> MemberHandlerCustomException(
            MemberException e) {
        MemberErrorResponse errorResponse = MemberErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .message(e.getErrorCode().getDescription())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AwsS3Exception.class)
    protected ResponseEntity<AwsS3ErrorResponse> MemberHandlerCustomException(
            AwsS3Exception e) {
        AwsS3ErrorResponse errorResponse = AwsS3ErrorResponse.builder()
                .message(e.getMessage())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
