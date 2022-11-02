package com.rezero.inandout.configuration.oauth.provider;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getProvider() {
        return "naver";
    }


    public String getGender() {
        return (String) attributes.get("gender");
    }

    public String getNickname() {
        return (String) attributes.get("nickname");
    }

    public String getBirthYear() {
        return (String) attributes.get("birthyear");    // 네 자리
    }


    public String getBirthday() {
        return (String) attributes.get("birthday"); // MM-dd 같은 식
    }


    public String getPhone() {
        return (String) attributes.get("mobile");
    }




    /**
     *
     * 닉네임, 폰번호, 생년월일, 성별을 추가해서 넣을 예정
     */


}
