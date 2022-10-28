package com.rezero.inandout.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.service.MemberService;
import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.service.ReportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(ReportController.class)
@DisplayName("ReportController 테스트")
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private ReportService reportService;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("월 지출 보고서 조회")
    void getExpenseMonthReport() throws Exception {
        //given
        Member member = Member.builder()
                .memberId(1L)
                .email("hgd@gmail.com")
                .password("1234")
                .build();

        User user = new User(member.getEmail(), member.getPassword(), AuthorityUtils.NO_AUTHORITIES);
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        List<ReportDto> reportDtos = Arrays.asList(
                ReportDto.builder()
                        .category("건강/문화")
                        .categorySum(8800000)
                        .categoryRatio(27.07)
                        .build(),
                ReportDto.builder()
                        .category("교통/차량")
                        .categorySum(23000000)
                        .categoryRatio(70.76)
                        .build(),
                ReportDto.builder()
                        .category("세금/이자")
                        .categorySum(200000)
                        .categoryRatio(0.62)
                        .build(),
                ReportDto.builder()
                        .category("식비")
                        .categorySum(41000)
                        .categoryRatio(0.13)
                        .build(),
                ReportDto.builder()
                        .category("의복/미용")
                        .categorySum(462000)
                        .categoryRatio(1.42)
                        .build()
        );

        given(reportService.getExpenseMonthReport(any(), any(), any()))
                .willReturn(reportDtos);

        //when
        //then
        mockMvc.perform(get("/api/report/month/expense?startDt=2020-10-01&endDt=2020-10-31")
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(testingAuthenticationToken)
                ).andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.[0].categorySum").value(8800000))
                .andExpect(jsonPath("$.[1].categorySum").value(23000000))
                .andExpect(jsonPath("$.[2].categorySum").value(200000))
                .andExpect(jsonPath("$.[3].categorySum").value(41000))
                .andExpect(jsonPath("$.[4].categorySum").value(462000));
    }
}