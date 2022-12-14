package com.rezero.inandout.member.service;

import static com.rezero.inandout.exception.errorcode.MemberErrorCode.CONFIRM_PASSWORD;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.MEMBER_NOT_EXIST;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.RESET_PASSWORD_KEY_EXPIRED;
import static com.rezero.inandout.exception.errorcode.MemberErrorCode.RESET_PASSWORD_KEY_NOT_EXIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.amazonaws.services.s3.AmazonS3Client;
import com.rezero.inandout.awss3.AwsS3Service;
import com.rezero.inandout.configuration.oauth.PrincipalOauth2UserService;
import com.rezero.inandout.exception.MemberException;
import com.rezero.inandout.exception.errorcode.MemberErrorCode;
import com.rezero.inandout.member.component.MailComponent;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.model.ChangePasswordInput;
import com.rezero.inandout.member.model.JoinMemberInput;
import com.rezero.inandout.member.model.LoginMemberInput;
import com.rezero.inandout.member.model.MemberDto;
import com.rezero.inandout.member.model.MemberStatus;
import com.rezero.inandout.member.model.ResetPasswordInput;
import com.rezero.inandout.member.model.UpdateMemberInput;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.member.service.impl.MemberServiceImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@DisplayName("MemberServiceImpl ?????????")
@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SecurityContextHolder securityContextHolder;

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private MailComponent mailComponent;

    @Mock
    private AwsS3Service awsS3Service;

    @Mock
    private AmazonS3Client amazonS3Client;

    @InjectMocks
    private MemberServiceImpl memberService;

    @InjectMocks
    PrincipalOauth2UserService principalOauth2UserService;

    @Test
    @DisplayName("????????????")
    void join() {

        // given
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());
        Member member = Member.builder().email("egg@naver.com").phone("010-2222-0000")
            .password(bCryptPasswordEncoder.encode("abc!@#12")).build();
        memberRepository.save(member);

        // when
        JoinMemberInput memberInput = JoinMemberInput.builder().email("egg@naver.com")
            .address("???????????????").phone("010-2222-0000")
            .birth(LocalDate.from(LocalDate.of(2000, 9, 30))).gender("???").nickName("??????")
            .password("abc!@#12").build();
        memberService.join(memberInput);

        // then
        verify(memberRepository, times(1)).save(member);

    }


    @Test
    @DisplayName("??????????????? ?????? ????????? ?????? - ??????")
    void emailAuth() {

        // given
        String uuid = UUID.randomUUID().toString();
        Member member = Member.builder().email("egg@naver.com").password("abc123!@")
            .emailAuthKey(uuid).status(MemberStatus.REQ).build();
        given(memberRepository.findByEmailAuthKey(anyString())).willReturn(Optional.of(member));

        // when
        memberService.emailAuth(uuid);

        // then
        assertEquals(MemberStatus.ING, member.getStatus());
    }


    @Test
    @DisplayName("??????????????? ?????? ????????? ?????? - ??????")
    void emailAuth_fail() {

        // given
        String uuid = UUID.randomUUID().toString();
        given(memberRepository.findByEmailAuthKey(anyString())).willReturn(Optional.empty());

        // when
        MemberException exception = assertThrows(MemberException.class,
            () -> memberService.emailAuth(uuid));

        // then
        assertEquals(MemberErrorCode.EMAIL_AUTH_KEY_NOT_EXIST, exception.getErrorCode());

    }


    @Test
    @DisplayName("????????? ?????? - ????????? ??????")
    void findEmail() {

        // given
        Member member = Member.builder().email("egg@naver.com").address("???????????????")
            .phone("010-2222-0000").birth(LocalDate.from(LocalDate.of(2000, 9, 30))).gender("???")
            .nickName("??????").password("abc!@#12").build();

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

        // when
        String email = "egg@naver.com";

        // then
        memberService.validateEmail(email);

    }


    @Test
    @DisplayName("???????????? ?????? - ?????????, ????????? ?????? ??????")
    void findPhone() {

        // given
        Member member = Member.builder().email("egg@naver.com").address("???????????????")
            .phone("010-2222-0000").birth(LocalDate.from(LocalDate.of(2000, 9, 30))).gender("???")
            .nickName("?????????").password("abc!@#12").build();
        given(memberRepository.findByEmailAndPhone(anyString(), anyString())).willReturn(
            Optional.of(member));

        // when
        String email = "egg@naver.com";
        String phone = "010-2222-0000";

        // then
        memberService.validatePhone(email, phone);

    }

    @Test
    @DisplayName("?????? ?????? ??????")
    void getInfo() {

        // given
        Member member = Member.builder().email("egg@naver.com").address("???????????????")
            .phone("010-2222-0000").birth(LocalDate.from(LocalDate.of(2000, 9, 30))).gender("???")
            .nickName("?????????").password("abc!@#12").memberS3ImageKey("key").build();

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

        // when
        MemberDto memberDto = memberService.getInfo(member.getEmail());

        // then
        assertEquals("?????????", memberDto.getNickName());

    }


    @Test
    @DisplayName("?????? ?????? ?????? - ??????")
    void updateInfo() {

        // given
        String s3ImageKey = "imageKey";
        Member member = Member.builder().email("egg@naver.com").address("???????????????")
            .phone("010-2222-0000").birth(LocalDate.from(LocalDate.of(2000, 9, 30))).gender("???")
            .nickName("?????????").password("abc!@#12")
            .memberS3ImageKey("2022-10-31T17:36:50.822 diary ?????????????????????.jpg").build();

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

        given(awsS3Service.addImageAndGetKey(any(), any()))
            .willReturn(s3ImageKey);

        // when
        UpdateMemberInput input = UpdateMemberInput.builder().address("?????????").nickName("??????")
            .phone("010-1111-2313").birth(LocalDate.now()).gender("???")
            .address("?????????").build();

        MockMultipartFile file = new MockMultipartFile("file",
            "test.png",
            "image/png",
            "?????png data>>".getBytes());

        // then
        memberService.updateInfo(member.getEmail(), input, file);

    }


    @Test
    @DisplayName("?????? ?????? ??????(?????? ??????) - ?????? (1)")
    void updateInfo_fail_blank() {

        // given
        String s3ImageKey = "imageKey";
        Member member = Member.builder().email("egg@naver.com").address("???????????????")
            .phone("010-2222-0000").birth(LocalDate.from(LocalDate.of(2000, 9, 30))).gender("???")
            .nickName("?????????").password("abc!@#12").memberS3ImageKey(s3ImageKey).build();

        given(memberRepository.findByEmail(anyString())).willReturn(
            Optional.of(member));       // ???????????? ????????? ????????????.

        UpdateMemberInput input = UpdateMemberInput.builder().address("?????????").nickName("??????")
            .phone("010-11 11-2313").birth(LocalDate.now()).gender("???")
            .address("?????????").build();

        MockMultipartFile file = new MockMultipartFile("file",
            "test.png",
            "image/png",
            "?????png data>>".getBytes());

        // when
        MemberException exception = assertThrows(MemberException.class,
            () -> memberService.updateInfo(member.getEmail(), input, file));

        // then
        assertEquals(MemberErrorCode.CONTAINS_BLANK.getDescription(),
            exception.getErrorCode().getDescription());

    }


    @Test
    @DisplayName("?????? ?????? ??????(?????? ???????????? ???????????? ??????) - ??????(2)")
    void updateInfo_fail_samePhoneNumber() {

        // given
        Member memberA = Member.builder().email("egg@naver.com").address("???????????????")
            .phone("010-2222-0000").birth(LocalDate.from(LocalDate.of(2000, 9, 30))).gender("???")
            .nickName("?????????").password("abc!@#12").build();
        given(memberRepository.findByEmail(any())).willReturn(Optional.of(memberA));

        Member memberB = Member.builder().email("ogh@naver.com").address("???????????????")
            .phone("010-9999-0000").birth(LocalDate.from(LocalDate.of(1998, 9, 30))).gender("???")
            .nickName("?????????").password("abc!@#12").build();
        given(memberRepository.findByNickName(any())).willReturn(Optional.of(memberB));

        UpdateMemberInput input = UpdateMemberInput.builder().address("?????????").nickName("?????????")
            .phone("010-2222-0000").birth(LocalDate.now()).gender("???")
            .address("?????????").build();

        // when
        MemberException exception = assertThrows(MemberException.class,
            () -> memberService.updateInfo(memberA.getEmail(), input, any()));

        // then
        assertEquals(MemberErrorCode.NICKNAME_EXIST.getDescription(),
            exception.getErrorCode().getDescription());

    }


    @Test
    @DisplayName("???????????? ?????? - ??????")
    void updatePassword() {

        // given
        Member member = Member.builder().email("egg@naver.com").address("???????????????")
            .phone("010-2222-0000").birth(LocalDate.from(LocalDate.of(2000, 9, 30))).gender("???")
            .nickName("?????????").build();
        String encPassword = bCryptPasswordEncoder.encode("abc!@#12");
        member.setPassword(encPassword);
        given(memberRepository.findByEmail("egg@naver.com")).willReturn(Optional.of(member));

        // when
        String email = "egg@naver.com";
        ChangePasswordInput input = ChangePasswordInput.builder().password("abc!@#12")
            .newPassword("xyz@#123").build();

        // then
        memberService.changePassword(email, input);

    }


    @Test
    @DisplayName("???????????? ?????? - ??????")
    void updatePassword_fail() {

        // given
        Member member = Member.builder().email("egg@naver.com").address("???????????????")
            .phone("010-2222-0000").birth(LocalDate.from(LocalDate.of(2000, 9, 30))).gender("???")
            .nickName("?????????").build();
        String encPassword = bCryptPasswordEncoder.encode("abc!@#12");
        member.setPassword(encPassword);
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

        // when
        String email = "egg@naver.com";
        ChangePasswordInput input = ChangePasswordInput.builder().password("abc!@#12")
            .newPassword("xyz").build();

        // then
        MemberException exception = assertThrows(MemberException.class,
            () -> memberService.changePassword(email, input));
        assertEquals(MemberErrorCode.PASSWORD_LENGTH_MORE_THAN_8.getDescription(),
            exception.getErrorCode().getDescription());

    }


    @Test
    @DisplayName("??????????????????")
    void login() {

        // given
        Member member = Member.builder().email("egg@naver.com").status(MemberStatus.ING).build();

        String encPassword = bCryptPasswordEncoder.encode("abc!@#12");
        member.setPassword(encPassword);
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        // when
        LoginMemberInput input = LoginMemberInput.builder().email("egg@naver.com")
            .password("abc!@#12").build();

        // then
        memberService.login(input);
    }


    @Test
    @DisplayName("?????????(????????? ??????)???????????????")
    void login_fail_id() {

        // given
        LoginMemberInput input = LoginMemberInput.builder().email("egg@naver.com")
            .password("abc!@#12").build();

        // when
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // then
        MemberException error = assertThrows(MemberException.class,
            () -> memberService.login(input));
        assertEquals(MEMBER_NOT_EXIST.getDescription(), error.getErrorCode().getDescription());

    }


    @Test
    @DisplayName("?????????(???????????? ??????)???????????????")
    void login_fail_pwd() {

        // given
        Member member = Member.builder().email("egg@naver.com").status(MemberStatus.ING).build();
        String encPassword = bCryptPasswordEncoder.encode("abc!@#12");
        member.setPassword(encPassword);
        given(memberRepository.findByEmail(member.getEmail())).willReturn(Optional.of(member));

        // when
        LoginMemberInput input = LoginMemberInput.builder().email("egg@naver.com")
            .password("abc!@#1200").build();

        // then
        MemberException exception = assertThrows(MemberException.class,
            () -> memberService.login(input));
        assertEquals(MemberErrorCode.PASSWORD_NOT_MATCH.getDescription(),
            exception.getErrorCode().getDescription());
    }


    @Test
    @DisplayName("???????????????- ??????")
    void logout() {

        // given
        User user = new User("egg@naver.com", "abc123!@", AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,
            null);

        // when
        securityContextHolder.getContext().setAuthentication(testingAuthenticationToken);
        memberService.logout();

        // then
        assertThrows(NullPointerException.class,
            () -> securityContextHolder.getContext().getAuthentication().getName());

    }


    @Test
    @DisplayName("????????????(??????????????? ?????? ??????) - ??????")
    void logout_fail() {

        // given
        securityContextHolder.getContext().setAuthentication(null);

        // when
        MemberException exception = assertThrows(MemberException.class,
            () -> memberService.logout());

        // then
        assertEquals(MemberErrorCode.CANNOT_LOGOUT.getDescription(),
            exception.getErrorCode().getDescription());
    }


    @Test
    @DisplayName("?????? ?????? - ??????")
    void withdraw() {

        // given
        Member member = Member.builder().email("egg@naver.com").status(MemberStatus.ING)
            .memberS3ImageKey("").build();
        String rawPassword = "abc123!@";
        member.setPassword(bCryptPasswordEncoder.encode(rawPassword));

        // when
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));
        memberService.withdraw(member.getEmail(), rawPassword);

        // then
        assertEquals(MemberStatus.WITHDRAW,
            memberRepository.findByEmail(member.getEmail()).get().getStatus());
    }


    @Test
    @DisplayName("?????? ?????? (????????? ??????) - ??????")
    void withdraw_fail_email() {

        // given
        Member member = Member.builder().email("egg@naver.com").status(MemberStatus.ING).build();
        String rawPassword = "abc123!@";
        member.setPassword(bCryptPasswordEncoder.encode(rawPassword));

        // when
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // then
        MemberException exception = assertThrows(MemberException.class,
            () -> memberService.withdraw(member.getEmail(), rawPassword));
        assertEquals(MemberErrorCode.EMAIL_NOT_EXIST, exception.getErrorCode());

    }


    @Test
    @DisplayName("?????? ?????? (???????????? ??????) - ??????")
    void withdraw_fail_pwd() {

        // given
        Member member = Member.builder().email("egg@naver.com").status(MemberStatus.ING).build();
        String rawPassword = "abc123!@";
        String wrongPassword = "xyz123!@";
        member.setPassword(bCryptPasswordEncoder.encode(rawPassword));

        // when
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

        // then
        MemberException exception = assertThrows(MemberException.class,
            () -> memberService.withdraw(member.getEmail(), wrongPassword));
        assertEquals(MemberErrorCode.PASSWORD_NOT_MATCH, exception.getErrorCode());

    }


    @Test
    @DisplayName("???????????? ????????? - ??????")
    void resetPassword() {

        // given
        ResetPasswordInput input = ResetPasswordInput.builder().newPassword("abc123!@")
            .confirmNewPassword("abc123!@").build();

        // when
        String uuid = UUID.randomUUID().toString();
        Member member = Member.builder().email("egg@naver.com").resetPasswordKey(uuid)
            .resetPasswordLimitDt(LocalDateTime.now().plusDays(1)).status(MemberStatus.ING).build();
        given(memberRepository.findByResetPasswordKey(anyString())).willReturn(Optional.of(member));

        // then
        memberService.resetPassword(uuid, input);
    }


    @Test
    @DisplayName("???????????? ????????? ?????? - (1) uuid ??????")
    void resetPassword_fail_uuid() {

        // given
        ResetPasswordInput input = ResetPasswordInput.builder().newPassword("abc123!@")
            .confirmNewPassword("abc123!@").build();

        // when
        String uuid = UUID.randomUUID().toString();
        given(memberRepository.findByResetPasswordKey(anyString())).willReturn(Optional.empty());

        // then
        MemberException exception = assertThrows(MemberException.class,
            () -> memberService.resetPassword(uuid, input));
        assertEquals(exception.getErrorCode(), RESET_PASSWORD_KEY_NOT_EXIST);
    }


    @Test
    @DisplayName("???????????? ????????? ?????? - (2) ???????????? ?????? ?????????")
    void resetPassword_fail_password_not_match() {

        // given
        ResetPasswordInput input = ResetPasswordInput.builder().newPassword("abc123!@")
            .confirmNewPassword("abc123!@#$%").build();

        // when
        String uuid = UUID.randomUUID().toString();
        Member member = Member.builder().email("egg@naver.com").resetPasswordKey(uuid)
            .resetPasswordLimitDt(LocalDateTime.now().plusDays(1)).status(MemberStatus.ING).build();
        given(memberRepository.findByResetPasswordKey(anyString())).willReturn(Optional.of(member));

        // then
        MemberException exception = assertThrows(MemberException.class,
            () -> memberService.resetPassword(uuid, input));
        assertEquals(exception.getErrorCode(), CONFIRM_PASSWORD);
    }


    @Test
    @DisplayName("???????????? ????????? ?????? - (3) ????????? ?????? ??????")
    void resetPassword_fail_resetDt_expired() {

        // given
        ResetPasswordInput input = ResetPasswordInput.builder().newPassword("abc123!@")
            .confirmNewPassword("abc123!@").build();

        // when
        String uuid = UUID.randomUUID().toString();
        Member member = Member.builder().email("egg@naver.com").resetPasswordKey(uuid)
            .resetPasswordLimitDt(LocalDateTime.now().minusDays(1)).status(MemberStatus.ING)
            .build();
        given(memberRepository.findByResetPasswordKey(anyString())).willReturn(Optional.of(member));

        // then
        MemberException exception = assertThrows(MemberException.class,
            () -> memberService.resetPassword(uuid, input));
        assertEquals(exception.getErrorCode(), RESET_PASSWORD_KEY_EXPIRED);
    }


}