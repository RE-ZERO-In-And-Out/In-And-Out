package com.rezero.inandout.exception;

import com.rezero.inandout.exception.errorcode.DiaryErrorCode;
import com.rezero.inandout.exception.errorcode.MemberErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DiaryException extends RuntimeException {
    public DiaryErrorCode errorCode;
    public String message;

    public DiaryException(String message) {
        this.message = message;
    }
}
