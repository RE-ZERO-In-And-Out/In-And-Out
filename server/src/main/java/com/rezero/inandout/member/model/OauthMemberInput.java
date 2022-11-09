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
public class OauthMemberInput {

    @NotBlank(message = " 'provider_providerId'의 형태로 저장해야합니다.")
    private String oauthUsername;

    @NotBlank(message = "닉네임을 정확하게 입력하세요.")
    private String nickName;

    @NotBlank(message = "연락처를 정확하게 입력하세요.")
    private String phone;

    @NotBlank(message = "provider를 정확하게 입력하세요.")
    private String provider;

    @NotBlank(message = "providerId를 정확하게 입력하세요.")
    private String providerId;

}
