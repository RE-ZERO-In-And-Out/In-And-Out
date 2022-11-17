package com.rezero.inandout.member.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberStatus {


    REQ("회원 가입 요청 중입니다."),
    ING("정상 회원입니다."),
    STOP("정지된 회원입니다."),
    WITHDRAW("탈퇴 회원입니다.");

    private final String description;
}
