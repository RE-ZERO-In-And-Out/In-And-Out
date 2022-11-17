package com.rezero.inandout.exception.response;

import com.rezero.inandout.exception.errorcode.IncomeErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IncomeErrorResponse {

    private IncomeErrorCode errorCode;
    private String message;

}
