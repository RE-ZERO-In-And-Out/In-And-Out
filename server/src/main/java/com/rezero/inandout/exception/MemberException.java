package com.rezero.inandout.exception;

import com.rezero.inandout.exception.errorcode.MemberErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberException extends RuntimeException {

    public MemberErrorCode errorCode;

}
