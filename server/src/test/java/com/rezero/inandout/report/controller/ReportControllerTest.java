package com.rezero.inandout.report.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rezero.inandout.income.repository.IncomeQueryRepository;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.member.service.MemberService;
import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.service.ReportService;
import com.rezero.inandout.report.service.impl.ReportServiceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(ReportController.class)
@DisplayName("ReportController 테스트")
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private IncomeQueryRepository incomeQueryRepository;

    @MockBean
    private MemberService memberService;

    @MockBean
    private ReportService reportService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("월 수입 보고서 조회")
    void getMonthlyIncomeReport() throws Exception {
        Member member = Member.builder()
            .memberId(1L)
            .password("1234")
            .email("test@naver.com")
            .build();

        ReportDto reportDto1 = ReportDto.builder()
            .category("주수입")
            .categorySum(1234567)
            .categoryRatio(80)
            .build();

        ReportDto reportDto2 = ReportDto.builder()
            .category("부수입")
            .categorySum(12345)
            .categoryRatio(20)
            .build();

        List<ReportDto> reportDtoList = new ArrayList<>(Arrays.asList(reportDto1, reportDto2));

        User user = new User(member.getEmail(), member.getPassword(), AuthorityUtils.createAuthorityList("ROLE_USER"));
        TestingAuthenticationToken testingAuthenticationToken
            = new TestingAuthenticationToken(user,null);

        given(reportService.getMonthlyIncomeReport(any(), any(), any()))
            .willReturn(reportDtoList);

        //when
        mockMvc.perform(
                get("/api/report/income/month?startDt=2022-10-01&endDt=2022-10-30")
                    .principal(testingAuthenticationToken))
            .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].category").value("주수입"))
                .andExpect(jsonPath("$.[1].categoryRatio").value(20.0));

        //then
        verify(reportService, times(1)).getMonthlyIncomeReport(any(), any(), any());
    }
}