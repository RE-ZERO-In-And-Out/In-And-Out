package com.rezero.inandout.configuration.oauth;

import com.rezero.inandout.configuration.auth.PrincipalDetails;
import com.rezero.inandout.configuration.oauth.provider.GoogleUserInfo;
import com.rezero.inandout.configuration.oauth.provider.NaverUserInfo;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.model.MemberRole;
import com.rezero.inandout.member.model.MemberStatus;
import com.rezero.inandout.member.model.OauthMemberInput;
import com.rezero.inandout.member.repository.MemberRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Member member = new Member();

        OauthMemberInput oauthMemberInput = OauthMemberInput.builder().build();

        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {

            GoogleUserInfo googleUserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
            String provider = googleUserInfo.getProvider();
            String providerId = googleUserInfo.getProviderId();
            String oauthUsername = provider + "_" + providerId;
            String encPwd = bCryptPasswordEncoder.encode(oauthUsername);

            Optional<Member> optionalMember = memberRepository.findByEmail(oauthUsername);

            if (!optionalMember.isPresent()) {

                member = Member.builder()
                    .email(oauthUsername)
                    .nickName(oauthUsername)
                    .phone(oauthMemberInput.getPhone())
                    .role(MemberRole.ROLE_OAUTH_MEMBER)
                    .memberS3ImageKey("")
                    .status(MemberStatus.ING).password(encPwd).build();

                memberRepository.save(member);
                log.info("[Member Signup for google] member: " + oauthUsername);

            } else {
                member = optionalMember.get();
                log.info("[Member Login for google] member: " + oauthUsername);

            }

        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            NaverUserInfo naverUserInfo = new NaverUserInfo(
                (Map) oAuth2User.getAttributes().get("response"));

            String provider = naverUserInfo.getProvider();
            String providerId = naverUserInfo.getProviderId();
            String oauthUsername = provider + "_" + providerId;
            String encPwd = bCryptPasswordEncoder.encode(oauthUsername);

            Optional<Member> optionalMember = memberRepository.findByEmail(oauthUsername);

            if (!optionalMember.isPresent()) {

                member = Member.builder()
                    .email(oauthUsername)
                    .nickName(oauthUsername)
                    .phone(naverUserInfo.getPhone())
                    .gender(naverUserInfo.getGender())
                    .role(MemberRole.ROLE_OAUTH_MEMBER)
                    .memberS3ImageKey("")
                    .status(MemberStatus.ING)
                    .password(encPwd).build();

                memberRepository.save(member);
                log.info("[Member Signup for naver] member: " + oauthUsername);

            } else {
                member = optionalMember.get();
                log.info("[Member Login for naver] member: " + oauthUsername);

            }

        }

        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }

}
