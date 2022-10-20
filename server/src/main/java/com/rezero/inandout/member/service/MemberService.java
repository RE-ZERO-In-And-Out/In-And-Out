package com.rezero.inandout.member.service;

import com.rezero.inandout.member.model.JoinMemberInput;
import com.rezero.inandout.member.model.UpdateMemberInput;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {

    /**
     * 회원 가입
     */
    void join(JoinMemberInput input);

    /**
     * 회원 수정
     */
    void update(String email, UpdateMemberInput input);


    /**
     * 회원 탈퇴
     */
    void withdraw(String email, String password);


    /**
     * 회원 아이디(email) 존재하는지 확인 - 아이디 찾기
     */
    String findEmail(String email);


    /**
     * 회원 아이디(email), phone 유효한지 확인 - 비밀번호 찾기
     */
    void findPhone(String email, String phone);


}
