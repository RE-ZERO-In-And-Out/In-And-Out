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
    boolean update(UpdateMemberInput input);


    /**
     * 회원 탈퇴
     */
    boolean withdraw(String email, String password);


}
