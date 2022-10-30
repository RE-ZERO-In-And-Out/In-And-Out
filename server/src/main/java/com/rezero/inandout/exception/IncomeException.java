package com.rezero.inandout.exception;

import com.rezero.inandout.exception.errorcode.IncomeErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IncomeException extends RuntimeException {

    public IncomeErrorCode errorCode;

}
