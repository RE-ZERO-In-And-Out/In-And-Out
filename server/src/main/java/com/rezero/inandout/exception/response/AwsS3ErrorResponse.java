package com.rezero.inandout.exception.response;

import com.rezero.inandout.exception.errorcode.DiaryErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AwsS3ErrorResponse {

    private String message;

}
