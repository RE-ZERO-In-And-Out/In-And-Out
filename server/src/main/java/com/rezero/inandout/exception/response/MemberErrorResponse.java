package com.rezero.inandout.exception.response;

import com.rezero.inandout.exception.errorcode.ExpenseErrorCode;
import com.rezero.inandout.exception.errorcode.MemberErrorCode;
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
public class MemberErrorResponse {

    private MemberErrorCode errorCode;
    private String message;

}
