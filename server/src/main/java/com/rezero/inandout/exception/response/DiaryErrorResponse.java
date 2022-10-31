package com.rezero.inandout.exception.response;

import com.rezero.inandout.exception.errorcode.DiaryErrorCode;
import com.rezero.inandout.exception.errorcode.ExpenseErrorCode;
import com.rezero.inandout.exception.errorcode.MemberErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiaryErrorResponse {

    private DiaryErrorCode errorCode;
    private String message;

}
