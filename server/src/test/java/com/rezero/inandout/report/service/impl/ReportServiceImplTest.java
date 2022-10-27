package com.rezero.inandout.report.service.impl;

import com.rezero.inandout.exception.ExpenseException;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.repository.ExpenseQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportServiceImpl 테스트")
class ReportServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ExpenseQueryRepository expenseQueryRepository;

    @InjectMocks
    private ReportServiceImpl reportServiceImpl;

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
        List<ReportDto> reportDtos = reportServiceImpl.getExpenseMonthReport(
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
        Member member = Member.builder()
                .memberId(1L)
                .email("hgd@gmail.com")
                .password("1234")
                .build();

        given(memberRepository.findByEmail(any()))
                .willReturn(Optional.empty());

        //when
        ExpenseException exception = assertThrows(ExpenseException.class,
                () -> reportServiceImpl.getExpenseMonthReport(
                "hgd@gmail.com",
                LocalDate.of(2022, 10, 1),
                LocalDate.of(2022, 10, 31)
                )
        );

        //then
        assertEquals(exception.getErrorCode().getDescription(), "없는 멤버입니다.");
    }
}