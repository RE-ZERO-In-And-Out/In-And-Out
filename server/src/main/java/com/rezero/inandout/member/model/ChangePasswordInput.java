package com.rezero.inandout.member.model;

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
public class ChangePasswordInput {


    @NotBlank(message = "기존 비밀번호을 정확하게 입력하세요.")
    private String password;

    @NotBlank(message = "새로운 비밀번호을 정확하게 입력하세요.")
    private String newPassword;


}
