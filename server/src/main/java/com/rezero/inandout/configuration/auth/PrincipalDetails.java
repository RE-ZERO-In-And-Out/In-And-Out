package com.rezero.inandout.configuration.auth;

import com.rezero.inandout.member.entity.Member;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Data
public class PrincipalDetails implements UserDetails, OAuth2User {      // 기존의  UserDetails와 같다

    private Member member;  // 컴포지션

    private Map<String, Object> attributes; // oauth 로그인 시 필요

    // 일반 로그인 시 사용
    public PrincipalDetails(Member member) {        // 로그인 시도하는 멤버 정보가 들어온다.
        this.member = member;
    }

    // oauth 로그인 시 사용
    public PrincipalDetails(Member member, Map<String, Object> attributes) {
        this.member = member;
        this.attributes = attributes;
    }

    // 해당 유저의 권한을 리턴하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collect = new ArrayList<>();
//        collect.add(new GrantedAuthority() {
//            @Override
//            public String getAuthority() {
//                return member.getRole();  // user의 권한을 반환한다.
//            }
//        });

        return collect;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {  // 하나의 비밀번호를 오래 사용했는지 여부
        return true;
    }

    @Override
    public boolean isEnabled() {
        // false인 경우는?
        // 1년 동안 회원이 로그인 안하는 경우 휴면 계정으로 전환된다고 가정한다. (user에 loginDate 변수를 추가한다.)
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        //return attributes.get("sub").toString();  // 안 쓸거라서 주석 처리
        return null;
    }
}
