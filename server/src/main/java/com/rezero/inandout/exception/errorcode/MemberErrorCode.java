package com.rezero.inandout.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode {

    /**
     * 회원가입 - 이미 존재하는 이메일, 휴대폰, 닉네임 오류
     */
    EXIST_EMAIL("이미 가입된 이메일입니다."),
    EXIST_PHONE("동일한 휴대폰 번호가 존재합니다. 다른 번호를 입력하세요."),
    EXIST_NICKNAME("동일한 닉네임이 존재합니다. 다른 닉네임을 입력하세요"),

    /**
     * 회원가입 - 비밀번호 오류
     */
    PASSWORD_LENGTH_MORE_THAN_8("비밀번호는 8자리 이상이어야합니다.(영문자, 숫자, 특수문자를 각각 1글자 이상 포함)"),
    PASSWORD_NOT_CONTAIN_DIGIT("비밀번호는 숫자를 한 글자 이상 포함해야합니다."),
    PASSWORD_NOT_CONTAIN_SPECIAL("비밀번호는 특수 문자를 한 글자 이상 포함해야합니다."),
    PASSWORD_NOT_CONTAIN_CHARACTER("비밀번호는 영문자를 한 글자 이상 포함해야합니다."),
    PASSWORD_NOT_CONTAIN_DIGIT_AND_SPECIAL("비밀번호는 숫자, 특수 문자를 각각 한 글자 이상 포함해야합니다."),
    PASSWORD_NOT_CONTAIN_DIGIT_AND_CHARACTER("비밀번호는 숫자, 영문자 문자를 각각 한 글자 이상 포함해야합니다."),
    PASSWORD_NOT_CONTAIN_CHARACTER_AND_SPECIAL("비밀번호는 영문자, 특수 문자를 각각 한 글자 이상 포함해야합니다."),

    /**
     * 회원 조회 오류
     */
    NOT_EXIST_MEMBER("회원 정보가 존재하지 않습니다."),
    NOT_EXIST_EMAIL("이메일이 존재하지 않습니다."),
    NOT_EXIST_PHONE("연락처(번호)가 존재하지 않습니다."),

    /**
     * 회원 정보 수정 오류
     */
    CONTAINS_BLANK("회원 정보는 공백을 포함할 수 없습니다."),

    /**
     * 비밀번호 오류
     */
    NOT_MATCH_PASSWORD("회원 비밀번호를 잘못 입력했습니다."),

    /**
     * 로그아웃 오류
     */
    NOT_LOGGED_IN("현재 로그인 상태가 아니어서 로그아읏 할 수 없습니다");


    private final String description;

}
