package com.rezero.inandout.member.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.model.JoinMemberInput;
import com.rezero.inandout.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@DisplayName("MemberServiceImpl 테스트")
@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
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
        String findEmail = memberService.findEmail(email);

        // then
        Assertions.assertEquals(email, findEmail);

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

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member));

        // when
        String email = "egg@naver.com";
        String phone = "010-2222-0000";
        String findPhone = memberService.findPhone(email, phone);

        // then
        Assertions.assertEquals(findPhone, phone);
    }
}