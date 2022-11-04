package com.rezero.inandout.diary.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.inandout.configuration.oauth.PrincipalOauth2UserService;
import com.rezero.inandout.diary.model.DeleteDiaryInput;
import com.rezero.inandout.diary.model.DiaryDto;
import com.rezero.inandout.diary.service.DiaryService;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.service.impl.MemberServiceImpl;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.web.multipart.MultipartFile;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(DiaryController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("DiaryController 테스트")
class DiaryControllerTest {

    @MockBean
    private DiaryService diaryService;

    @MockBean
    private MemberServiceImpl memberService;

    @MockBean
    PrincipalOauth2UserService principalOauth2UserService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("일기 조회 테스트")
    void getDiaryList() throws Exception {
        //given
        Member member = Member.builder()
            .memberId(1L)
            .email("hgd@gmail.com")
            .password("1234")
            .build();

        User user = new User(member.getEmail(), member.getPassword(),
            AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,
            null);

        List<DiaryDto> diaryDtos = Arrays.asList(
            DiaryDto.builder()
                .diaryId(1L)
                .nickName("홍길동")
                .diaryDt(LocalDate.of(2020, 10, 1))
                .text("굿")
                .s3ImageUrl("amazon~~")
                .build(),
            DiaryDto.builder()
                .diaryId(2L)
                .nickName("홍길동")
                .diaryDt(LocalDate.of(2020, 10, 2))
                .text("굿")
                .s3ImageUrl("amazon~~")
                .build(),
            DiaryDto.builder()
                .diaryId(3L)
                .nickName("홍길동")
                .diaryDt(LocalDate.of(2020, 10, 3))
                .text("굿")
                .s3ImageUrl("amazon~~")
                .build()
        );
        given(diaryService.getDiaryList(any(), any(), any()))
            .willReturn(diaryDtos);

        //when
        //then
        mockMvc.perform(
                get("/api/diary?startDt=2022-10-01&endDt=2022-10-31")
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(testingAuthenticationToken)
            ).andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.[0].diaryId").value(1L))
            .andExpect(jsonPath("$.[1].diaryId").value(2L))
            .andExpect(jsonPath("$.[2].diaryId").value(3L));
    }

    @Test
    @DisplayName("일기 저장 테스트")
    void addDiary() throws Exception {
        //given
        Member member = Member.builder()
            .memberId(1L)
            .email("hgd@gmail.com")
            .password("1234")
            .build();

        User user = new User(member.getEmail(), member.getPassword(),
            AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,
            null);

        MockMultipartFile file1 = new MockMultipartFile("file",
            "test.png",
            "image/png",
            "«‹png data>>".getBytes());

        MockMultipartFile file2 = new MockMultipartFile("input",
            "", "application/json",
            ("{\"diaryDt\" : \"2022-10-01\"," + " \"text\" : \"강아지 귀엽다 ㅎㅎ\"}")
                .getBytes(StandardCharsets.UTF_8));

        //when
        mockMvc.perform(
                multipart("/api/diary")
                    .file(file1)
                    .file(file2)
                    .principal(testingAuthenticationToken)
            ).andExpect(status().isOk())
            .andDo(print());

        ArgumentCaptor<LocalDate> captorDiaryDt = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<String> captorText = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MultipartFile> captor2 = ArgumentCaptor.forClass(MultipartFile.class);
        //then
        verify(diaryService, times(1))
            .addDiary(any(), captorDiaryDt.capture(),
                captorText.capture(), captor2.capture());
    }

    @Test
    @DisplayName("일기 수정 테스트")
    void updateDiary() throws Exception {
        //given
        Member member = Member.builder()
            .memberId(1L)
            .email("hgd@gmail.com")
            .password("1234")
            .build();

        User user = new User(member.getEmail(), member.getPassword(),
            AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,
            null);

        MockMultipartFile file1 = new MockMultipartFile("file",
            "test.png",
            "image/png",
            "«‹png data>>".getBytes());

        MockMultipartFile file2 = new MockMultipartFile("input",
            "", "application/json",
            ("{\"diaryDt\" : \"2022-10-01\"," + " \"text\" : \"강아지 귀엽다 ㅎㅎ\"}")
                .getBytes(StandardCharsets.UTF_8));
        //when
        mockMvc.perform(
                multipart(HttpMethod.PUT, "/api/diary")
                    .file(file1)
                    .file(file2)
                    .principal(testingAuthenticationToken)
            ).andExpect(status().isOk())
            .andDo(print());

        ArgumentCaptor<LocalDate> captorDiaryDt = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<String> captorText = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MultipartFile> captor2 = ArgumentCaptor.forClass(MultipartFile.class);
        //then
        verify(diaryService, times(1))
            .updateDiary(any(), any(), captorDiaryDt.capture(),
                captorText.capture(), captor2.capture());
    }

    @Test
    @DisplayName("일기 삭제 테스트")
    void deleteDiary() throws Exception {
        //given
        Member member = Member.builder()
            .memberId(1L)
            .email("hgd@gmail.com")
            .password("1234")
            .build();

        User user = new User(member.getEmail(), member.getPassword(),
            AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,
            null);

        DeleteDiaryInput input = DeleteDiaryInput.builder()
            .diaryId(1L)
            .build();

        //when
        mockMvc.perform(
                delete("/api/diary")
                    .contentType(MediaType.APPLICATION_JSON)
                    .principal(testingAuthenticationToken)
                    .content(objectMapper.writeValueAsString(input))
            ).andExpect(status().isOk())
            .andDo(print());

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        //then
        verify(diaryService, times(1))
            .deleteDiary(any(), captor.capture());
    }
}