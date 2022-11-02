package com.rezero.inandout.member.service;

import com.rezero.inandout.member.model.ChangePasswordInput;
import com.rezero.inandout.member.model.JoinMemberInput;
import com.rezero.inandout.member.model.LoginMemberInput;
import com.rezero.inandout.member.model.MemberDto;
import com.rezero.inandout.member.model.ResetPasswordInput;
import com.rezero.inandout.member.model.UpdateMemberInput;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService extends UserDetailsService  {

    /**
     * 회원 가입
     */
    void join(JoinMemberInput input);

    /**
     * 이메일 인증
     */
    void emailAuth(String uuid);

    /**
     * 회원 로그인
     */
    void login(LoginMemberInput input);


    /**
     * 회원 로그아웃
     */
    void logout();


    /**
     * 회원 탈퇴
     */
    void withdraw(String email, String password);


    /**
     * 회원 아이디(email) 존재하는지 확인 - 아이디 찾기
     */
    void validateEmail(String email);


    /**
     * 회원 아이디(email), phone 유효한지 확인 - 비밀번호 찾기
     */
    void validatePhone(String email, String phone);


    /**
     * 회원 비밀번호 초기화 - 이전 비밀번호는 확인하지 않고 이메일 인증으로 대체
     */
    void resetPassword(String uuid, ResetPasswordInput input);


    /**
     * 회원 조회
     */
    MemberDto getInfo(String email);


    /**
     * 회원 정보 수정
     */
    void updateInfo(String email, UpdateMemberInput input, MultipartFile file);


    /**
     * 회원 비밀번호 수정 - 이전 비밀번호를 확인
     */
    void changePassword(String email, ChangePasswordInput input);

}
