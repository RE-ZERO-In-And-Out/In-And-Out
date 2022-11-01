package com.rezero.inandout.exception;

import com.rezero.inandout.exception.errorcode.DiaryErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DiaryException extends RuntimeException {
    public DiaryErrorCode errorCode;
}
