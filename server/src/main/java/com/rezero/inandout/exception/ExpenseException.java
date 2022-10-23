package com.rezero.inandout.exception;

import com.rezero.inandout.exception.errorcode.ExpenseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExpenseException extends RuntimeException {

    public ExpenseErrorCode errorCode;

}
