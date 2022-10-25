package com.rezero.inandout.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.model.ChangePasswordInput;
import com.rezero.inandout.member.model.JoinMemberInput;
import com.rezero.inandout.member.model.MemberDto;
import com.rezero.inandout.member.model.UpdateMemberInput;
import com.rezero.inandout.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@DisplayName("MemberServiceImpl 테스트")
@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Test
    @DisplayName("회원가입")
    void join() {

        // given
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());
        Member member = Member.builder()
            .email("egg@naver.com")
            .phone("010-2222-0000")
            .password(bCryptPasswordEncoder.encode("abc!@#12"))
            .build();
        memberRepository.save(member);

        // when
        JoinMemberInput memberInput = JoinMemberInput.builder()
            .email("egg@naver.com")
            .address("서울특별시")
            .phone("010-2222-0000")
            .birth(LocalDate.from(LocalDate.of(2000, 9, 30)))
            .gender("남")
            .nickName("원빈")
            .password("abc!@#12")
            .build();
        memberService.join(memberInput);

        // then
        verify(memberRepository, times(1)).save(member);

    }

    @Test
    @DisplayName("아이디 찾기 - 이메일 확인")
    void findEmail() {

        // given
        Member member = Member.builder()
            .email("egg@naver.com")
            .address("서울특별시")
            .phone("010-2222-0000")
            .birth(LocalDate.from(LocalDate.of(2000, 9, 30)))
            .gender("남")
            .nickName("원빈")
            .password("abc!@#12")
            .build();

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

        // when
        String email = "egg@naver.com";

        // then
        memberService.validateEmail(email);

    }

    @Test
    @DisplayName("비밀번호 찾기 - 이메일, 휴대폰 일치 확인")
    void findPhone() {

        // given
        Member member = Member.builder()
            .email("egg@naver.com")
            .address("서울특별시")
            .phone("010-2222-0000")
            .birth(LocalDate.from(LocalDate.of(2000, 9, 30)))
            .gender("남")
            .nickName("강동원")
            .password("abc!@#12")
            .build();
        given(memberRepository.findByEmailAndPhone(anyString(), anyString())).willReturn(
            Optional.of(member));

        // when
        String email = "egg@naver.com";
        String phone = "010-2222-0000";

        // then
        memberService.validatePhone(email, phone);

    }

    @Test
    @DisplayName("회원 정보 조회")
    void getInfo() {

        // given
        Member member = Member.builder()
            .email("egg@naver.com")
            .address("서울특별시")
            .phone("010-2222-0000")
            .birth(LocalDate.from(LocalDate.of(2000, 9, 30)))
            .gender("남")
            .nickName("강동원")
            .password("abc!@#12")
            .build();
        given(memberRepository.findByEmail(anyString())).willReturn(
            Optional.of(member)
        );

        // when
        MemberDto memberDto = memberService.getInfo(member.getEmail());

        // then
        assertEquals("강동원", memberDto.getNickName());

    }


    @Test
    @DisplayName("회원 정보 수정 - 성공")
    void updateInfo() {

        // given
        Member member = Member.builder()
            .email("egg@naver.com")
            .address("서울특별시")
            .phone("010-2222-0000")
            .birth(LocalDate.from(LocalDate.of(2000, 9, 30)))
            .gender("남")
            .nickName("강동원")
            .password("abc!@#12")
            .build();
        given(memberRepository.findByEmail(anyString())).willReturn(
            Optional.of(member)
        );

        // when
        UpdateMemberInput input = UpdateMemberInput.builder().address("강원도")
            .nickName("치킨")
            .phone("010-1111-2313")
            .birth(LocalDate.now())
            .memberPhotoUrl("c:")
            .gender("여")
            .address("강원도")
            .build();

        // then
        memberService.updateInfo("egg@naver.com", input);

    }


    @Test
    @DisplayName("회원 정보 수정(공백 포함) - 실패 (1)")
    void updateInfo_fail_blank() {

        // given
        Member member = Member.builder()
            .email("egg@naver.com")
            .address("서울특별시")
            .phone("010-2222-0000")
            .birth(LocalDate.from(LocalDate.of(2000, 9, 30)))
            .gender("남")
            .nickName("강동원")
            .password("abc!@#12")
            .build();
        given(memberRepository.findByEmail(anyString())).willReturn(
            Optional.of(member)
        );
        UpdateMemberInput input = UpdateMemberInput.builder().address("강원도")
            .nickName("치킨")
            .phone("010-11 11-2313")
            .birth(LocalDate.now())
            .memberPhotoUrl("c:")
            .gender("여")
            .address("강원도")
            .build();

        // when
        Exception exception = assertThrows(RuntimeException.class,
            () -> memberService.updateInfo(member.getEmail(), input));

        // then
        assertEquals(exception.getMessage(), "회원 정보는 공백을 포함할 수 없습니다.");

    }


    @Test
    @DisplayName("회원 정보 수정(이전의 닉네임과 동일) - 실패 (2)")
    void updateInfo_fail_sameNickName() {

        // given
        Member member = Member.builder()
            .email("egg@naver.com")
            .address("서울특별시")
            .phone("010-2222-0000")
            .birth(LocalDate.from(LocalDate.of(2000, 9, 30)))
            .gender("남")
            .nickName("강동원")
            .password("abc!@#12")
            .build();
        given(memberRepository.findByEmail("egg@naver.com")).willReturn(
            Optional.of(member)
        );

        UpdateMemberInput input = UpdateMemberInput.builder().address("강원도")
            .nickName("강동원")
            .phone("010-1111-2313")
            .birth(LocalDate.now())
            .memberPhotoUrl("c:")
            .gender("여")
            .address("강원도")
            .build();

        // when
        Exception exception = assertThrows(RuntimeException.class,
            () -> memberService.updateInfo(member.getEmail(), input));

        // then
        assertEquals(exception.getMessage(), "기존 닉네임과 동일합니다.");

    }


    @Test
    @DisplayName("회원 정보 수정(이전의 폰번호와 동일) - 실패(3)")
    void updateInfo_fail_samePhoneNumber() {

        // given
        Member member = Member.builder()
            .email("egg@naver.com")
            .address("서울특별시")
            .phone("010-2222-0000")
            .birth(LocalDate.from(LocalDate.of(2000, 9, 30)))
            .gender("남")
            .nickName("강동원")
            .password("abc!@#12")
            .build();
        given(memberRepository.findByEmail(any())).willReturn(
            Optional.of(member)
        );
        // when
        UpdateMemberInput input = UpdateMemberInput.builder().address("강원도")
            .nickName("동원참치")
            .phone("010-2222-0000")
            .birth(LocalDate.now())
            .memberPhotoUrl("c:")
            .gender("여")
            .address("강원도")
            .build();

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> memberService.updateInfo(member.getEmail(), input));

        // then
        assertEquals(exception.getMessage(), "기존 연락처와 동일합니다.");


    }


    @Test
    @DisplayName("비밀번호 변경 - 성공")
    void updatePassword() {

        // given
        Member member = Member.builder()
            .email("egg@naver.com")
            .address("서울특별시")
            .phone("010-2222-0000")
            .birth(LocalDate.from(LocalDate.of(2000, 9, 30)))
            .gender("남")
            .nickName("강동원")
            .build();
        String encPassword = bCryptPasswordEncoder.encode("abc!@#12");
        member.setPassword(encPassword);
        given(memberRepository.findByEmail("egg@naver.com")).willReturn(
            Optional.of(member)
        );

        // when
        String email = "egg@naver.com";
        ChangePasswordInput input = ChangePasswordInput.builder()
            .password("abc!@#12")
            .newPassword("xyz@#123")
            .build();

        // then
        memberService.changePassword(email, input);


    }


    @Test
    @DisplayName("비밀번호 변경 - 실패")
    void updatePassword_fail() {

        // given
        Member member = Member.builder()
            .email("egg@naver.com")
            .address("서울특별시")
            .phone("010-2222-0000")
            .birth(LocalDate.from(LocalDate.of(2000, 9, 30)))
            .gender("남")
            .nickName("강동원")
            .build();
        String encPassword = bCryptPasswordEncoder.encode("abc!@#12");
        member.setPassword(encPassword);
        given(memberRepository.findByEmail(anyString())).willReturn(
            Optional.of(member)
        );

        // when

        String email = "egg@naver.com";
        ChangePasswordInput input = ChangePasswordInput.builder()
            .password("abc!@#12")
            .newPassword("xyz")
            .build();

        // then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> memberService.changePassword(email, input));
        assertEquals(exception.getMessage(), "비밀번호는 8자리 이상이어야합니다.(영문자, 숫자, 특수문자를 각각 1글자 이상 포함)");
    }


}