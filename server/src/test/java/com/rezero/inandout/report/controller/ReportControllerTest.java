package com.rezero.inandout.report.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rezero.inandout.configuration.oauth.PrincipalOauth2UserService;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.service.MemberServiceImpl;
import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.model.YearlyExpenseReportDto;
import com.rezero.inandout.report.model.YearlyIncomeReportDto;
import com.rezero.inandout.report.model.YearlyTotalReportDto;
import com.rezero.inandout.report.service.ReportService;
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
import org.springframework.http.MediaType;
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
    private MemberServiceImpl memberService;


    @MockBean
    PrincipalOauth2UserService principalOauth2UserService;


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

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

        User user = new User(member.getEmail(), member.getPassword(),
            AuthorityUtils.createAuthorityList("ROLE_USER"));
        TestingAuthenticationToken testingAuthenticationToken
            = new TestingAuthenticationToken(user, null);

        given(reportService.getMonthlyIncomeReport(any(), any(), any()))
            .willReturn(reportDtoList);

        //when
        mockMvc.perform(
                get("/api/report/month/income?startDt=2022-10-01&endDt=2022-10-30")
                    .principal(testingAuthenticationToken))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].category").value("주수입"))
            .andExpect(jsonPath("$.[1].categoryRatio").value(20.0));

        //then
        verify(reportService, times(1)).getMonthlyIncomeReport(any(), any(), any());
    }

    @Test
    @DisplayName("월 지출 보고서 조회")
    void getExpenseMonthReport() throws Exception {
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

        given(reportService.getMonthlyExpenseReport(any(), any(), any()))
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

    @Test
    @DisplayName("연 지출 보고서 조회")
    void getYearlyExpenseReport() throws Exception {
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

        List<YearlyExpenseReportDto> yearlyReportDtos = Arrays.asList(
            YearlyExpenseReportDto.builder()
                .year(2022)
                .month(1)
                .monthlySum(0)
                .build(),
            YearlyExpenseReportDto.builder()
                .year(2022)
                .month(2)
                .monthlySum(0)
                .build(),
            YearlyExpenseReportDto.builder()
                .year(2022)
                .month(3)
                .monthlySum(0)
                .build(),
            YearlyExpenseReportDto.builder()
                .year(2022)
                .month(4)
                .monthlySum(0)
                .build(),
            YearlyExpenseReportDto.builder()
                .year(2022)
                .month(5)
                .monthlySum(0)
                .build(),
            YearlyExpenseReportDto.builder()
                .year(2022)
                .month(6)
                .monthlySum(0)
                .build(),
            YearlyExpenseReportDto.builder()
                .year(2022)
                .month(7)
                .monthlySum(0)
                .build(),
            YearlyExpenseReportDto.builder()
                .year(2022)
                .month(8)
                .monthlySum(0)
                .build(),
            YearlyExpenseReportDto.builder()
                .year(2022)
                .month(9)
                .monthlySum(0)
                .build(),
            YearlyExpenseReportDto.builder()
                .year(2022)
                .month(10)
                .monthlySum(8800000 + 23000000 + 200000 + 41000 + 462000)
                .expenseReport(reportDtos)
                .build(),
            YearlyExpenseReportDto.builder()
                .year(2022)
                .month(11)
                .monthlySum(0)
                .build(),
            YearlyExpenseReportDto.builder()
                .year(2022)
                .month(12)
                .monthlySum(0)
                .build()
        );
        given(reportService.getYearlyExpenseReport(any(), any(), any()))
            .willReturn(yearlyReportDtos);
        //when
        //then
        mockMvc.perform(get("/api/report/year/expense?startDt=2022-01-01&endDt=2022-12-31")
                .contentType(MediaType.APPLICATION_JSON)
                .principal(testingAuthenticationToken)
            ).andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.[9].year").value(2022))
            .andExpect(jsonPath("$.[9].month").value(10))
            .andExpect(
                jsonPath("$.[9].monthlySum").value(8800000 + 23000000 + 200000 + 41000 + 462000))
            .andExpect(jsonPath("$.[9].expenseReport[0].categorySum").value(8800000))
            .andExpect(jsonPath("$.[9].expenseReport[1].categorySum").value(23000000))
            .andExpect(jsonPath("$.[9].expenseReport[2].categorySum").value(200000))
            .andExpect(jsonPath("$.[9].expenseReport[3].categorySum").value(41000))
            .andExpect(jsonPath("$.[9].expenseReport[4].categorySum").value(462000));
    }


    @Test
    @DisplayName("연 수입 보고서 조회")
    void getYearlyIncomeReport() throws Exception {

        Member member = Member.builder()
            .memberId(1L)
            .password("1234")
            .email("test@naver.com")
            .build();

        ReportDto reportDto1 = ReportDto.builder()
            .category("10월주수입")
            .categorySum(2000)
            .categoryRatio(80)
            .build();

        ReportDto reportDto2 = ReportDto.builder()
            .category("10월부수입")
            .categorySum(200)
            .categoryRatio(20)
            .build();

        List<ReportDto> reportDtoList_ten = new ArrayList<>(Arrays.asList(reportDto1, reportDto2));

        ReportDto reportDto3 = ReportDto.builder()
            .category("11월주수입")
            .categorySum(1000)
            .categoryRatio(30)
            .build();

        ReportDto reportDto4 = ReportDto.builder()
            .category("11월부수입")
            .categorySum(1500)
            .categoryRatio(70)
            .build();

        List<ReportDto> reportDtoList_ele = new ArrayList<>(Arrays.asList(reportDto3, reportDto4));

        List<YearlyIncomeReportDto> yearlyReportDtoList = new ArrayList<>(Arrays.asList(
            YearlyIncomeReportDto.builder().year(2022).month(1).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(2).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(3).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(4).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(5).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(6).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(7).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(8).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(9).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(10).incomeReport(reportDtoList_ten)
                .build(),
            YearlyIncomeReportDto.builder().year(2022).month(11).incomeReport(reportDtoList_ele)
                .build(),
            YearlyIncomeReportDto.builder().year(2022).month(12).incomeReport(null).build())
        );

        given(reportService.getYearlyIncomeReport(any(), any(), any()))
            .willReturn(yearlyReportDtoList);

        User user = new User(member.getEmail(), member.getPassword(),
            AuthorityUtils.createAuthorityList("ROLE_USER"));
        TestingAuthenticationToken testingAuthenticationToken
            = new TestingAuthenticationToken(user, null);

        //when
        //then
        mockMvc.perform(
                get("/api/report/year/income?startDt=2022-01-01&endDt=2022-12-31")
                    .principal(testingAuthenticationToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].year").value(2022))
            .andExpect(jsonPath("$.[0].month").value(1))
            .andExpect(jsonPath("$.[11].year").value(2022))
            .andExpect(jsonPath("$.[11].month").value(12))
            .andExpect(jsonPath("$.[9].incomeReport.[0].category").value("10월주수입"))
            .andExpect(jsonPath("$.[10].incomeReport.[1].category").value("11월부수입"))
            .andDo(print());

        verify(reportService, times(1)).getYearlyIncomeReport(any(), any(), any());

    }

    @Test
    @DisplayName("연 보고서 조회")
    void getYearlyTotalReport() throws Exception {
        Member member = Member.builder()
            .memberId(1L)
            .password("1234")
            .email("test@naver.com")
            .build();

        User user = new User(member.getEmail(), member.getPassword(),
            AuthorityUtils.createAuthorityList("ROLE_USER"));
        TestingAuthenticationToken testingAuthenticationToken
            = new TestingAuthenticationToken(user, null);

        ReportDto reportDto1 = ReportDto.builder()
            .category("10월주수입").categorySum(2000).categoryRatio(80).build();

        ReportDto reportDto2 = ReportDto.builder()
            .category("10월부수입").categorySum(200).categoryRatio(20).build();

        List<ReportDto> reportDtoList_ten = new ArrayList<>(Arrays.asList(reportDto1, reportDto2));

        ReportDto reportDto3 = ReportDto.builder()
            .category("11월주수입").categorySum(1000).categoryRatio(30).build();

        ReportDto reportDto4 = ReportDto.builder()
            .category("11월부수입").categorySum(1500).categoryRatio(70).build();

        List<ReportDto> reportDtoList_ele = new ArrayList<>(Arrays.asList(reportDto3, reportDto4));

        List<YearlyIncomeReportDto> yearlyIncomeReportDtoList = new ArrayList<>(Arrays.asList(
            YearlyIncomeReportDto.builder().year(2022).month(1).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(2).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(3).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(4).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(5).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(6).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(7).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(8).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(9).incomeReport(null).build(),
            YearlyIncomeReportDto.builder().year(2022).month(10).incomeReport(reportDtoList_ten)
                .build(),
            YearlyIncomeReportDto.builder().year(2022).month(11).incomeReport(reportDtoList_ele)
                .build(),
            YearlyIncomeReportDto.builder().year(2022).month(12).incomeReport(null).build())
        );

        given(reportService.getYearlyIncomeReport(any(), any(), any()))
            .willReturn(yearlyIncomeReportDtoList);

        List<ReportDto> reportDtos = Arrays.asList(
            ReportDto.builder().category("건강/문화").categorySum(8800000).categoryRatio(27.07).build(),
            ReportDto.builder().category("교통/차량").categorySum(23000000).categoryRatio(70.76)
                .build(),
            ReportDto.builder().category("세금/이자").categorySum(200000).categoryRatio(0.62).build(),
            ReportDto.builder().category("식비").categorySum(41000).categoryRatio(0.13).build(),
            ReportDto.builder().category("의복/미용").categorySum(462000).categoryRatio(1.42).build()
        );

        List<YearlyExpenseReportDto> yearlyExpenseReportDtos = Arrays.asList(
            YearlyExpenseReportDto.builder().year(2022).month(1).monthlySum(0).build(),
            YearlyExpenseReportDto.builder().year(2022).month(2).monthlySum(0).build(),
            YearlyExpenseReportDto.builder().year(2022).month(3).monthlySum(0).build(),
            YearlyExpenseReportDto.builder().year(2022).month(4).monthlySum(0).build(),
            YearlyExpenseReportDto.builder().year(2022).month(5).monthlySum(0).build(),
            YearlyExpenseReportDto.builder().year(2022).month(6).monthlySum(0).build(),
            YearlyExpenseReportDto.builder().year(2022).month(7).monthlySum(0).build(),
            YearlyExpenseReportDto.builder().year(2022).month(8).monthlySum(0).build(),
            YearlyExpenseReportDto.builder().year(2022).month(9).monthlySum(0).build(),
            YearlyExpenseReportDto.builder().year(2022).month(10)
                .monthlySum(8800000 + 23000000 + 200000 + 41000 + 462000).expenseReport(reportDtos)
                .build(),
            YearlyExpenseReportDto.builder().year(2022).month(11).monthlySum(0).build(),
            YearlyExpenseReportDto.builder().year(2022).month(12).monthlySum(0).build()
        );

        given(reportService.getYearlyExpenseReport(any(), any(), any()))
            .willReturn(yearlyExpenseReportDtos);

        YearlyTotalReportDto yearlyTotalReportDto = YearlyTotalReportDto.builder()
            .incomeReportList(yearlyIncomeReportDtoList)
            .expenseReportList(yearlyExpenseReportDtos)
            .build();

        given(reportService.getYearlyTotalReport(any(), any(), any()))
            .willReturn(yearlyTotalReportDto);

        //when
        //then
        mockMvc.perform(
                get("/api/report/year?startDt=2022-01-01&endDt=2022-12-31")
                    .principal(testingAuthenticationToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.incomeReportList.[0].year")
                .value(2022))
            .andExpect(jsonPath("$.incomeReportList.[9].incomeReport.[0].category")
                .value("10월주수입"))
            .andExpect(jsonPath("$.expenseReportList.[9].month")
                .value(10))
            .andExpect(jsonPath("$.expenseReportList.[9].monthlySum")
                .value(8800000 + 23000000 + 200000 + 41000 + 462000))
            .andDo(print());

    }
}