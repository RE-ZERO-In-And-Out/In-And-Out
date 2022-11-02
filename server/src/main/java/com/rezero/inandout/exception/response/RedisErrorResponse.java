package com.rezero.inandout.exception.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisErrorResponse {

    private String message;

}
