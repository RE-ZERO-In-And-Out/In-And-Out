package com.rezero.inandout.member.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.inandout.configuration.oauth.PrincipalOauth2UserService;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.model.ChangePasswordInput;
import com.rezero.inandout.member.model.FindPasswordMemberInput;
import com.rezero.inandout.member.model.JoinMemberInput;
import com.rezero.inandout.member.model.LoginMemberInput;
import com.rezero.inandout.member.model.MemberStatus;
import com.rezero.inandout.member.model.ResetPasswordInput;
import com.rezero.inandout.member.model.UpdateMemberInput;
import com.rezero.inandout.member.model.WithdrawMemberInput;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.member.service.MemberService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(MemberController.class)
@DisplayName("MemberController ?????????")
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private MemberService memberServiceImpl;


    @MockBean
    PrincipalOauth2UserService principalOauth2UserService;

    @Test
    @DisplayName("????????????")
    void signUp() throws Exception {

        // given
        JoinMemberInput memberInput = JoinMemberInput.builder().email("egg@naver.com")
            .address("???????????????").phone("010-2222-0000")
            .birth(LocalDate.from(LocalDate.of(2000, 9, 30))).gender("???").nickName("??????")
            .password("1").build();
        String memberInputJson = mapper.writeValueAsString(memberInput);

        //when
        mockMvc.perform(
                post("/api/signup").contentType(MediaType.APPLICATION_JSON).content(memberInputJson))
            .andExpect(status().isOk()).andDo(print());
        ArgumentCaptor<JoinMemberInput> captor = ArgumentCaptor.forClass(JoinMemberInput.class);

        //then
        verify(memberServiceImpl, times(1)).join(captor.capture());
        assertEquals(captor.getValue().getEmail(), memberInput.getEmail());

    }


    @Test
    @DisplayName("?????? ????????? ?????? ????????? ??????")
    void emailAuth() throws Exception {

        // given
        String uuid = UUID.randomUUID().toString();

        // when
        mockMvc.perform(
                post("/api/signup/sending?id=" + uuid).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(print());
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        // then
        verify(memberServiceImpl, times(1)).emailAuth(captor.capture());
        assertEquals(uuid, captor.getValue());

    }


    @Test
    @DisplayName("?????????(?????????) ?????? - ?????? ?????? ??????")
    void checkEmail() throws Exception {

        // given
        FindPasswordMemberInput memberInput = FindPasswordMemberInput.builder()
            .email("egg@naver.com").build();
        String inputToJson = mapper.writeValueAsString(memberInput);

        // when
        mockMvc.perform(post("/api/password/email").contentType(MediaType.APPLICATION_JSON)
            .content(inputToJson)).andExpect(status().isOk()).andDo(print());
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        // then
        verify(memberServiceImpl, times(1)).validateEmail(captor.capture());
        assertEquals(captor.getValue(), memberInput.getEmail());
    }

    @Test
    @DisplayName("???????????? ??????(?????????, ????????? ??????) - ??????")
    void checkPhone() throws Exception {

        // given
        FindPasswordMemberInput memberInput = FindPasswordMemberInput.builder()
            .email("egg@naver.com").phone("010-2345-1234").build();
        String inputToJson = mapper.writeValueAsString(memberInput);

        //when
        mockMvc.perform(post("/api/password/email/phone").contentType(MediaType.APPLICATION_JSON)
            .content(inputToJson)).andExpect(status().isOk()).andDo(print());
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        //then
        verify(memberServiceImpl, times(1)).validatePhone(anyString(), captor.capture());
        assertEquals(captor.getValue(), memberInput.getPhone());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ??????")
    void getInfo() throws Exception {

        // given
        Member member = Member.builder().email("egg@naver.com").address("???????????????")
            .phone("010-2222-0000").birth(LocalDate.from(LocalDate.of(2000, 9, 30))).gender("???")
            .nickName("??????").password("egg123!@")
            .memberS3ImageKey(
                "https://inandoutimagebucket.s3.ap-northeast-2.amazonaws.com/2022-11-01T14%3A12%3A30.514978900%20member%20%EA%B2%8C%EB%8D%94.png")
            .build();
        String inputToJson = mapper.writeValueAsString(member);

        User user = new User(member.getEmail(), member.getPassword(),
            AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,
            null);

        // when
        mockMvc.perform(get("/api/member/info").contentType(MediaType.APPLICATION_JSON)
                .principal(testingAuthenticationToken).content(inputToJson)).andExpect(status().isOk())
            .andDo(print());
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        // then
        verify(memberServiceImpl, times(1)).getInfo(captor.capture());
        assertEquals(captor.getValue(), member.getEmail());
    }


    @Test
    @DisplayName("?????? ?????? ?????? - ??????")
    void updateInfo() throws Exception {

        // given
        Member member = Member.builder().email("egg@naver.com")
            .password("123abc?!")
            .build();
        User user = new User(member.getEmail(), member.getPassword(),
            AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,
            null);

        MockMultipartFile file = new MockMultipartFile("file",
            "dog.png",
            "image/png",
            "?????png data>>".getBytes());

        MockMultipartFile input = new MockMultipartFile("input",
            "", "application/json",
            ("{ \"nickName\" : \"??????\","
                + " \"phone\" : \"010-1111-1111\","
                + " \"address\" : \"???????????????\","
                + " \"gender\" : \"???\"}").getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(
            multipart(HttpMethod.PUT, "/api/member/info")
                .file(file)
                .file(input)
                .principal(testingAuthenticationToken)
        ).andExpect(status().isOk()).andDo(print());

        ArgumentCaptor<UpdateMemberInput> captorInput = ArgumentCaptor.forClass(
            UpdateMemberInput.class);
        ArgumentCaptor<MultipartFile> captorFile = ArgumentCaptor.forClass(MultipartFile.class);
        verify(memberServiceImpl, times(1)).updateInfo(anyString(), captorInput.capture(),
            captorFile.capture());

    }


    @Test
    @DisplayName("?????? ???????????? ?????? - ??????")
    void updatePassword() throws Exception {

        // given
        ChangePasswordInput input = ChangePasswordInput.builder().password("abc123!@")
            .newPassword("xyz098?!").build();
        String inputToJson = mapper.writeValueAsString(input);
        String email = "egg@naver.com";
        String pwd = "123abc?!";

        User user = new User(email, pwd, AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,
            null);

        //when
        mockMvc.perform(patch("/api/member/password").contentType(MediaType.APPLICATION_JSON)
                .principal(testingAuthenticationToken).content(inputToJson)).andExpect(status().isOk())
            .andDo(print());
        ArgumentCaptor<ChangePasswordInput> captor = ArgumentCaptor.forClass(
            ChangePasswordInput.class);

        // then
        verify(memberServiceImpl, times(1)).changePassword(anyString(), captor.capture());
        assertEquals(captor.getValue().getNewPassword(), input.getNewPassword());

    }


    @Test
    @DisplayName("????????? - ??????")
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
        verify(memberServiceImpl, times(1)).login(captor.capture());
        assertEquals(captor.getValue().getPassword(), input.getPassword());

    }


    @Test
    @DisplayName("???????????? - ??????")
    void signout() throws Exception {

        // given
        Member member = Member.builder().email("egg@naver.com").password("abc123~!").build();
        String inputToJson = mapper.writeValueAsString(member);
        User user = new User(member.getEmail(), member.getPassword(),
            AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,
            null);

        // when
        mockMvc.perform(
            post("/api/signout").contentType(MediaType.APPLICATION_JSON).content(inputToJson)
                .principal(testingAuthenticationToken)).andExpect(status().isOk()).andDo(print());

        // then
        verify(memberServiceImpl, times(1)).logout();

    }


    @Test
    @DisplayName("?????? ?????? - ??????")
    void delete() throws Exception {

        // given
        Member member = Member.builder().email("egg@naver.com").password("abc123~!")
            .status(MemberStatus.ING).build();
        WithdrawMemberInput input = WithdrawMemberInput.builder().password("abc123~!").build();
        String withdrawInputToJson = mapper.writeValueAsString(input);
        User user = new User(member.getEmail(), member.getPassword(),
            AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,
            null);

        // when
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/member/info")
            .contentType(MediaType.APPLICATION_JSON).content(withdrawInputToJson)
            .principal(testingAuthenticationToken)).andExpect(status().isOk()).andDo(print());
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        // then
        verify(memberServiceImpl, times(1)).withdraw(anyString(), captor.capture());
        assertEquals(input.getPassword(), captor.getValue());

    }


    @Test
    @DisplayName("?????? ???????????? ?????????")
    void resetPassword() throws Exception {

        // given
        ResetPasswordInput input = ResetPasswordInput.builder().newPassword("abc123!@")
            .confirmNewPassword("abc123!@").build();
        String inputToJson = mapper.writeValueAsString(input);

        // when
        String uuid = UUID.randomUUID().toString();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/password/email/phone/sending?id=" + uuid)
                .contentType(MediaType.APPLICATION_JSON).content(inputToJson))
            .andExpect(status().isOk()).andDo(print());
        ArgumentCaptor<ResetPasswordInput> captor = ArgumentCaptor.forClass(
            ResetPasswordInput.class);

        // then
        verify(memberServiceImpl, times(1)).resetPassword(anyString(), captor.capture());
        assertEquals(input.getNewPassword(), captor.getValue().getNewPassword());

    }


}