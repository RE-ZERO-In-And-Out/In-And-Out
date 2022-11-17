package com.rezero.inandout.income.repository;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rezero.inandout.calendar.model.CalendarIncomeDto;
import com.rezero.inandout.income.entity.QIncome;
import com.rezero.inandout.income.entity.QIncomeCategory;
import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.model.YearlyReportDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class IncomeQueryRepositoryTest {

    @InjectMocks
    IncomeQueryRepository incomeQueryRepository;

    @Mock
    JPAQueryFactory queryFactory;

    @Test
    @DisplayName("월 수입 보고서 조회")
    void getMonthlyIncomeReport_success() {

        //given
        QIncome income = new QIncome("i");
        QIncomeCategory incomeCategory = new QIncomeCategory("ic");

        List<ReportDto> reportDtoList = new ArrayList<>();

        ReportDto reportDto1 = ReportDto.builder()
            .category("주수입")
            .categorySum(1234567)
            .categoryRatio(80.0)
            .build();

        ReportDto reportDto2 = ReportDto.builder()
            .category("부수입")
            .categorySum(12345)
            .categoryRatio(20.0)
            .build();

        reportDtoList.add(reportDto1);
        reportDtoList.add(reportDto2);

        JPAQuery step1 = mock(JPAQuery.class);
        given(queryFactory
            .select(Projections.constructor(ReportDto.class,
            incomeCategory.incomeCategoryName,
            income.incomeAmount.sum(),
            income.incomeAmount.sum().doubleValue())
            )
        ).willReturn(step1);

        JPAQuery step2 = mock(JPAQuery.class);
        given(step1.from(any(EntityPath.class)))
            .willReturn(step2);

        JPAQuery step3 = mock(JPAQuery.class);
        given(step2.leftJoin(any(EntityPath.class), any(EntityPath.class)))
            .willReturn(step3);

        JPAQuery step4 = mock(JPAQuery.class);
        given(step3.where(any(BooleanExpression.class)))
            .willReturn(step4);

        JPAQuery step5 = mock(JPAQuery.class);
        given(step4.groupBy(incomeCategory.incomeCategoryName))
            .willReturn(step5);

        JPAQuery step6 = mock(JPAQuery.class);
        given(step5.orderBy(income.incomeDt.asc()))
            .willReturn(step6);

        JPAQuery step7 = mock(JPAQuery.class);
        given(step6.orderBy(income.incomeAmount.sum().desc()))
                .willReturn(step7);

        given(step7.fetch())
            .willReturn(reportDtoList);

        //when
        List<ReportDto> queryReportDtoList
            = incomeQueryRepository.getMonthlyIncomeReport(1L,
            LocalDate.of(2022,10,1),
            LocalDate.of(2022,10,31)
        );

        //then
        assertEquals(queryReportDtoList.get(0).getCategory(), "주수입");
        assertEquals(queryReportDtoList.get(0).getCategorySum(), 1234567);
        assertEquals(queryReportDtoList.get(0).getCategoryRatio(), 80.0);
        assertEquals(queryReportDtoList.get(1).getCategory(), "부수입");
        assertEquals(queryReportDtoList.get(1).getCategorySum(), 12345);
        assertEquals(queryReportDtoList.get(1).getCategoryRatio(), 20.0);
        assertEquals(queryReportDtoList.size(), 2);
    }

    @Test
    void getYearlyIncomeReport() {
        //given
        QIncome income = new QIncome("i");
        QIncomeCategory incomeCategory = new QIncomeCategory("ic");

        List<YearlyReportDto> result = Arrays.asList(
                YearlyReportDto.builder()
                        .year(2022)
                        .month(1)
                        .category("카테고리1")
                        .categorySum(5000)
                        .categoryRatio(5000.0)
                        .build(),
                YearlyReportDto.builder()
                        .year(2022)
                        .month(2)
                        .category("카테고리2")
                        .categorySum(6000)
                        .categoryRatio(6000.0)
                        .build(),
                YearlyReportDto.builder()
                        .year(2022)
                        .month(3)
                        .category("카테고리3")
                        .categorySum(7000)
                        .categoryRatio(7000.0)
                        .build()
        );

        JPAQuery resultStep1 = Mockito.mock(JPAQuery.class);
        given(queryFactory.select(
                Projections.constructor(
                        YearlyReportDto.class,
                        income.incomeDt.year(),
                        income.incomeDt.month(),
                        incomeCategory.incomeCategoryName,
                        income.incomeAmount.sum(),
                        income.incomeAmount.sum().multiply(100).doubleValue()
                ))
        ).willReturn(resultStep1);

        JPAQuery resultStep2 = Mockito.mock(JPAQuery.class);
        given(resultStep1.from(any(EntityPath.class)))
                .willReturn(resultStep2);

        JPAQuery resultStep3 = Mockito.mock(JPAQuery.class);
        given(resultStep2.leftJoin(income.detailIncomeCategory.incomeCategory,
                incomeCategory)).willReturn(resultStep3);

        JPAQuery resultStep4 = Mockito.mock(JPAQuery.class);
        given(resultStep3.where(any(BooleanExpression.class)
                        ,any(BooleanExpression.class)
                )).willReturn(resultStep4);

        JPAQuery resultStep5 = Mockito.mock(JPAQuery.class);
        given(resultStep4.groupBy(income.incomeDt.month(),
                incomeCategory.incomeCategoryName))
                .willReturn(resultStep5);

        JPAQuery resultStep6 = Mockito.mock(JPAQuery.class);
        given(resultStep5.orderBy(
                income.incomeDt.year().asc(),
                income.incomeDt.month().asc(),
                income.incomeAmount.sum().desc()))
                .willReturn(resultStep6);

        given(resultStep6.fetch())
                .willReturn(result);

        //when
        List<YearlyReportDto> yearlyReportDtos = incomeQueryRepository
                .getYearlyIncomeReport(
                        1L,
                        LocalDate.of(2022, 1, 1),
                        LocalDate.of(2022, 3, 31)
                );

        //then
        assertEquals(yearlyReportDtos.get(0).getCategorySum(), 5000);
        assertEquals(yearlyReportDtos.get(1).getCategorySum(), 6000);
        assertEquals(yearlyReportDtos.get(2).getCategorySum(), 7000);
    }

    @Test
    @DisplayName("달력 월 수입내역 조회")
    void getMonthlyIncomeCalendar_success() {

        //given
        QIncome income = new QIncome("i");
        QIncomeCategory incomeCategory = new QIncomeCategory("ic");

        List<CalendarIncomeDto> calendarIncomeDtoList = new ArrayList<>(Arrays.asList(
            CalendarIncomeDto.builder().incomeDt(LocalDate.of(2022, 10, 2))
                .item("수입1").amount(123456).build(),
            CalendarIncomeDto.builder().incomeDt(LocalDate.of(2022, 10, 28))
                .item("수입2").amount(54321).build()
        ));

        JPAQuery step1 = mock(JPAQuery.class);
        given(queryFactory.select(Projections.constructor(CalendarIncomeDto.class,
                income.incomeDt, income.incomeItem, income.incomeAmount)))
            .willReturn(step1);

        JPAQuery step2 = mock(JPAQuery.class);
        given(step1.from(income))
            .willReturn(step2);

        JPAQuery step3 = mock(JPAQuery.class);
        given(step2.where(
            any(BooleanExpression.class)))
            .willReturn(step3);

        JPAQuery step4 = mock(JPAQuery.class);
        given(step3.orderBy(
            income.incomeDt.asc()))
            .willReturn(step4);

        given(step4.fetch())
            .willReturn(calendarIncomeDtoList);

        //when
        List<CalendarIncomeDto> getCalendarIncomeDtoList
            = incomeQueryRepository.getMonthlyIncomeCalendar(1L,
            LocalDate.of(2022,10,1),
            LocalDate.of(2022,10,31));

        //then
        assertEquals(getCalendarIncomeDtoList, calendarIncomeDtoList);
    }
}