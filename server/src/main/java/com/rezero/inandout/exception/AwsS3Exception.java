package com.rezero.inandout.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AwsS3Exception extends RuntimeException {
    public String message;
}
