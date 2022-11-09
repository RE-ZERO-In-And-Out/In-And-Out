package com.rezero.inandout.member.model;

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

    private String oauthUsername;
    private String nickName;
    private String phone;
    private String provider;
    private String providerId;


}
