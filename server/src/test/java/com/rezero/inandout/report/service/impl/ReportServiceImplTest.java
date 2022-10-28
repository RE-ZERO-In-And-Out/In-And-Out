package com.rezero.inandout.report.service.impl;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.rezero.inandout.exception.MemberException;
import com.rezero.inandout.exception.errorcode.MemberErrorCode;
import com.rezero.inandout.income.repository.IncomeQueryRepository;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.report.model.ReportDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import com.rezero.inandout.exception.ExpenseException;
import com.rezero.inandout.report.repository.ExpenseQueryRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    IncomeQueryRepository incomeQueryRepository;

    @Mock
    private ExpenseQueryRepository expenseQueryRepository;

    @InjectMocks
    ReportServiceImpl reportServiceImpl;

    @Nested
    @DisplayName("월 수입 보고서 조회 서비스 테스트")
    class getMonthlyIncomeReportMethod {

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

        @Test
        @DisplayName("성공")
        void getMonthlyIncomeReport_success() {
            //given
            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.of(member));

            given(incomeQueryRepository.getMonthlyIncomeReport(any(), any(), any()))
                .willReturn(reportDtoList);

            //when
            List<ReportDto> getReportDtoList
                = reportServiceImpl.getMonthlyIncomeReport(member.getEmail(),
                LocalDate.of(2022, 10, 1),
                LocalDate.of(2022, 10, 31));

            //then
            verify(incomeQueryRepository, times(1))
                .getMonthlyIncomeReport(any(), any(), any());
            assertEquals(getReportDtoList.get(0).getCategorySum(),
                reportDtoList.get(0).getCategorySum());
            assertEquals(getReportDtoList.get(1).getCategoryRatio(),
                reportDtoList.get(1).getCategoryRatio());
            assertEquals(getReportDtoList.size(), 2);
        }

        @Test
        @DisplayName("실패 - 멤버 없음")
        void getMonthlyIncomeReport_fail_no_member() {
            //given
            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.empty());

            //when
            MemberException exception = assertThrows(MemberException.class,
                () -> reportServiceImpl.getMonthlyIncomeReport(member.getEmail(),
                LocalDate.of(2022, 10, 1),
                LocalDate.of(2022, 10, 31)));

            //then
            assertEquals(exception.getErrorCode(), MemberErrorCode.NOT_EXIST_MEMBER);
        }    
    }
    
    @Test
    @DisplayName("월 지출 보고서 조회 - 성공")
    void getExpenseMonthReport_success() {
        //given
        Member member = Member.builder()
                .memberId(1L)
                .email("hgd@gmail.com")
                .password("1234")
                .build();

        given(memberRepository.findByEmail(any()))
                .willReturn(Optional.of(member));


        List<ReportDto> result = Arrays.asList(
                new ReportDto("건강/문화", 8800000, 27.07),
                new ReportDto("교통/차량", 23000000, 70.76),
                new ReportDto("세금/이자", 200000, 0.62),
                new ReportDto("식비", 41000, 0.13),
                new ReportDto("의복/미용", 462000, 1.42)
        );

        given(expenseQueryRepository.getExpenseMonthReport(any(), any(), any()))
                .willReturn(result);

        //when
        List<ReportDto> reportDtos = reportServiceImpl.getMonthlyExpenseReport(
                "hgd@gmail.com",
                LocalDate.of(2022, 10, 1),
                LocalDate.of(2022, 10, 31)
        );

        //then
        assertEquals(8800000, reportDtos.get(0).getCategorySum());
        assertEquals(23000000, reportDtos.get(1).getCategorySum());
        assertEquals(200000, reportDtos.get(2).getCategorySum());
        assertEquals(41000, reportDtos.get(3).getCategorySum());
        assertEquals(462000, reportDtos.get(4).getCategorySum());
    }

    @Test
    @DisplayName("월 지출 보고서 조회 - 실패")
    void getExpenseMonthReport_fail_() {
        //given
        given(memberRepository.findByEmail(any()))
                .willReturn(Optional.empty());

        //when
        MemberException exception = assertThrows(MemberException.class,
                () -> reportServiceImpl.getMonthlyExpenseReport(
                "hgd@gmail.com",
                LocalDate.of(2022, 10, 1),
                LocalDate.of(2022, 10, 31)
                )
        );

        //then
        assertEquals(exception.getErrorCode(), MemberErrorCode.NOT_EXIST_MEMBER);
    }
}