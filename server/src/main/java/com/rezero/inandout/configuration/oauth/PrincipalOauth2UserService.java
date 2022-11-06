package com.rezero.inandout.configuration.oauth;

import com.rezero.inandout.configuration.auth.PrincipalDetails;
import com.rezero.inandout.configuration.oauth.provider.GoogleUserInfo;
import com.rezero.inandout.configuration.oauth.provider.NaverUserInfo;
import com.rezero.inandout.exception.MemberException;
import com.rezero.inandout.exception.errorcode.MemberErrorCode;
import com.rezero.inandout.member.entity.Member;
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


    public OauthMemberInput validateOauthInput(OauthMemberInput input) {

        OauthMemberInput oauthMember = OauthMemberInput.builder().build();
        String oauthUsername = input.getProvider() + "_" + input.getProviderId();
        Optional<Member> optionalMember = memberRepository.findByEmail(input.getEmail());

        if (optionalMember.isPresent()) {
            oauthMember.setEmail(oauthUsername);
            throw new MemberException(MemberErrorCode.EMAIL_EXIST);

        } else {
            oauthMember.setEmail(input.getEmail());
        }

        optionalMember = memberRepository.findByPhone(input.getPhone());
        if (optionalMember.isPresent()) {
            oauthMember.setPhone(null);
        } else {
            oauthMember.setPhone(input.getPhone());
        }

        optionalMember = memberRepository.findByNickName(input.getNickName());
        if (optionalMember.isPresent()) {
            oauthMember.setNickName(oauthUsername);
        } else {
            oauthMember.setNickName(input.getNickName());
        }

        return oauthMember;
    }

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

            oauthMemberInput.setProvider(provider);
            oauthMemberInput.setProviderId(providerId);
            oauthMemberInput.setEmail(googleUserInfo.getEmail());
            oauthMemberInput.setNickName(googleUserInfo.getName());

            Optional<Member> optionalMember = memberRepository.findByProviderAndProviderId(provider,
                providerId);

            if (!optionalMember.isPresent()) {

                oauthMemberInput = validateOauthInput(oauthMemberInput);

                member = Member.builder().oauthUsername(oauthUsername)
                    .email(oauthMemberInput.getEmail()).nickName(oauthMemberInput.getNickName())
                    .phone(oauthMemberInput.getPhone()).provider(provider).providerId(providerId)
                    .status(MemberStatus.ING).password(encPwd).build();

                memberRepository.save(member);
                log.info("[Member Signup for google] member: " + googleUserInfo.getEmail());

            } else {
                member = optionalMember.get();
                log.info("[Member Login for google] member: " + googleUserInfo.getEmail());

            }

        } else if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            NaverUserInfo naverUserInfo = new NaverUserInfo(
                (Map) oAuth2User.getAttributes().get("response"));

            String provider = naverUserInfo.getProvider();
            String providerId = naverUserInfo.getProviderId();
            String oauthUsername = provider + "_" + providerId;
            String encPwd = bCryptPasswordEncoder.encode(oauthUsername);

            oauthMemberInput.setProvider(provider);
            oauthMemberInput.setProviderId(providerId);
            oauthMemberInput.setEmail(naverUserInfo.getEmail());
            oauthMemberInput.setNickName(naverUserInfo.getName());

            Optional<Member> optionalMember = memberRepository.findByProviderAndProviderId(provider,
                providerId);

            if (!optionalMember.isPresent()) {

                oauthMemberInput = validateOauthInput(oauthMemberInput);

                member = Member.builder().oauthUsername(oauthUsername)
                    .email(oauthMemberInput.getEmail()).nickName(oauthMemberInput.getNickName())
                    .phone(oauthMemberInput.getPhone()).gender(naverUserInfo.getGender())
                    .provider(provider).providerId(providerId).status(MemberStatus.ING)
                    .password(encPwd).build();

                memberRepository.save(member);
                log.info("[Member Signup for naver] member: " + naverUserInfo.getEmail());

            } else {
                member = optionalMember.get();
                log.info("[Member Login for naver] member: " + naverUserInfo.getEmail());

            }

        }

        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }

}
