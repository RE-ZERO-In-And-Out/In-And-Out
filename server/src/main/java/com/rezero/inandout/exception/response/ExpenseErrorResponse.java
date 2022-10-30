package com.rezero.inandout.exception.response;

import com.rezero.inandout.exception.errorcode.ExpenseErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseErrorResponse {

    private ExpenseErrorCode errorCode;
    private String message;

}
