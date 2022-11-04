package com.rezero.inandout.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RedisException extends RuntimeException {
    public String message;
}
