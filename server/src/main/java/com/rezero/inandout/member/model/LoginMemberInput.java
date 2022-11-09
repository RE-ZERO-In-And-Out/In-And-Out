package com.rezero.inandout.member.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginMemberInput {

    @Email(message = "email을 정확하게 입력하세요.")
    private String email;

    @NotBlank(message = "영문자, 숫자, 특수 기호를 한 글자 이상 넣으셔야합니다.")
    private String password;


}
