package com.rezero.inandout.member.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.inandout.member.model.FindPasswordMemberInput;
import com.rezero.inandout.member.model.JoinMemberInput;
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
        Mockito.verify(memberServiceImpl, times(1)).findEmail(captor.capture());
        assertEquals(captor.getValue(), memberInput.getEmail());
    }

    @Test
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
        Mockito.verify(memberServiceImpl, times(1)).findPhone(anyString(), captor.capture());
        assertEquals(captor.getValue(), memberInput.getPhone());
    }
}