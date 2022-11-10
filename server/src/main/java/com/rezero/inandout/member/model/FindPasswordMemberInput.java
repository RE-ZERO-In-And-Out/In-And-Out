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
public class FindPasswordMemberInput {

    @Email(message = "email을 정확하게 입력하세요.")
    private String email;

    @NotBlank(message = "연락처를 정확하게 입력하세요.")
    private String phone;

}
