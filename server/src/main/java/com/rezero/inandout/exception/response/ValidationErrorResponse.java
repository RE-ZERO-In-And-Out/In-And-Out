package com.rezero.inandout.exception.response;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidationErrorResponse {

    private String message;

}
