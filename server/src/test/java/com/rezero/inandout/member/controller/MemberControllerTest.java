package com.rezero.inandout.member.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.model.ChangePasswordInput;
import com.rezero.inandout.member.model.FindPasswordMemberInput;
import com.rezero.inandout.member.model.JoinMemberInput;
import com.rezero.inandout.member.model.LoginMemberInput;
import com.rezero.inandout.member.model.UpdateMemberInput;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.member.service.MemberServiceImpl;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(MemberController.class)
@DisplayName("MemberController 테스트")
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private MemberServiceImpl memberServiceImpl;

    @MockBean
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입")
    void signUp() throws Exception {

        // given
        JoinMemberInput memberInput = JoinMemberInput.builder()
            .email("egg@naver.com")
            .address("서울특별시")
            .phone("010-2222-0000")
            .birth(LocalDate.from(LocalDate.of(2000, 9, 30)))
            .gender("남")
            .nickName("원빈")
            .password("1")
            .build();
        String memberInputJson = mapper.writeValueAsString(memberInput);

        //when
        mockMvc.perform(
                post("/api/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(memberInputJson))
            .andExpect(status().isOk())
            .andDo(print());
        ArgumentCaptor<JoinMemberInput> captor = ArgumentCaptor.forClass(JoinMemberInput.class);

        //then
        Mockito.verify(memberServiceImpl, times(1)).join(captor.capture());
        assertEquals(captor.getValue().getEmail(), memberInput.getEmail());

    }

    @Test
    @DisplayName("아이디(이메일) 찾기 - 존재 여부 확인")
    void checkEmail() throws Exception {

        // given
        FindPasswordMemberInput memberInput = FindPasswordMemberInput.builder()
            .email("egg@naver.com")
            .build();
        String inputToJson = mapper.writeValueAsString(memberInput);

        // when
        mockMvc.perform(
                post("/api/password/email")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(inputToJson))
            .andExpect(status().isOk())
            .andDo(print());
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        // then
        Mockito.verify(memberServiceImpl, times(1)).validateEmail(captor.capture());
        assertEquals(captor.getValue(), memberInput.getEmail());
    }

    @Test
    @DisplayName("비밀번호 찾기(이메일, 폰번호 확인) - 성공")
    void checkPhone() throws Exception {

        // given
        FindPasswordMemberInput memberInput = FindPasswordMemberInput.builder()
            .email("egg@naver.com")
            .phone("010-2345-1234")
            .build();
        String inputToJson = mapper.writeValueAsString(memberInput);

        //when
        mockMvc.perform(
                post("/api/password/email/phone")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(inputToJson))
            .andExpect(status().isOk())
            .andDo(print());
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        //then
        Mockito.verify(memberServiceImpl, times(1)).validatePhone(anyString(), captor.capture());
        assertEquals(captor.getValue(), memberInput.getPhone());
    }

    @Test
    @DisplayName("회원 정보 조회 - 성공")
    void getInfo() throws Exception {

        // given
        Member member = Member
            .builder()
            .email("egg@naver.com")
            .address("서울특별시")
            .phone("010-2222-0000")
            .birth(LocalDate.from(LocalDate.of(2000, 9, 30)))
            .gender("남")
            .nickName("원빈")
            .password("1")
            .build();
        String inputToJson = mapper.writeValueAsString(member);

        // when
        mockMvc.perform(
                get("/api/member/info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(inputToJson))
            .andExpect(status().isOk())
            .andDo(print());
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        // then
        Mockito.verify(memberServiceImpl, times(1)).getInfo(captor.capture());
        assertEquals(captor.getValue(), member.getEmail());
    }


    @Test
    @DisplayName("회원 정보 수정 - 성공")
    void updateInfo() throws Exception {

        // given
        UpdateMemberInput input = UpdateMemberInput.builder().address("강원도")
            .nickName("치킨")
            .phone("010-1111-2313")
            .birth(LocalDate.now())
            .memberPhotoUrl("c:")
            .gender("여")
            .address("강원도")
            .build();
        String inputToJson = mapper.writeValueAsString(input);

        // when
        mockMvc.perform(
                put("/api/member/info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(inputToJson))
            .andExpect(status().isOk())
            .andDo(print());
        ArgumentCaptor<UpdateMemberInput> captor = ArgumentCaptor.forClass(UpdateMemberInput.class);

        // then
        Mockito.verify(memberServiceImpl, times(1)).updateInfo(anyString(), captor.capture());
        assertEquals(captor.getValue().getPhone(), input.getPhone()); //
    }


    @Test
    @DisplayName("회원 비밀번호 변경 - 성공")
    void updatePassword() throws Exception {

        // given
        ChangePasswordInput input = ChangePasswordInput.builder()
            .password("abc123!@")
            .newPassword("xyz098?!")
            .build();
        String inputToJson = mapper.writeValueAsString(input);

        //when
        mockMvc.perform(
                patch("/api/member/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(inputToJson))
            .andExpect(status().isOk())
            .andDo(print());
        ArgumentCaptor<ChangePasswordInput> captor = ArgumentCaptor.forClass(
            ChangePasswordInput.class);

        // then
        Mockito.verify(memberServiceImpl, times(1)).changePassword(anyString(), captor.capture());
        assertEquals(captor.getValue().getNewPassword(), input.getNewPassword());

    }

    @Test
    @DisplayName("로그인 - 성공")
    void signin() throws Exception {

        // given
        LoginMemberInput input = LoginMemberInput.builder().email("egg@naver.com")
            .password("abc123~!").build();
        String inputToJson = mapper.writeValueAsString(input);

        // when
        mockMvc.perform(
                post("/api/signin").contentType(MediaType.APPLICATION_JSON).content(inputToJson))
            .andExpect(status().isOk()).andDo(print());

        ArgumentCaptor<LoginMemberInput> captor = ArgumentCaptor.forClass(LoginMemberInput.class);

        // then
        Mockito.verify(memberServiceImpl, times(1)).login(captor.capture());
        assertEquals(captor.getValue().getPassword(), input.getPassword());

    }


}