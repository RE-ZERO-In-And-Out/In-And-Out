package com.rezero.inandout.income.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rezero.inandout.income.entity.QIncome;
import com.rezero.inandout.income.entity.QIncomeCategory;
import com.rezero.inandout.report.model.ReportDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
            income.incomeAmount.sum()
                .multiply(100)
                .doubleValue())
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
        given(step5.orderBy(income.incomeAmount.sum().desc()))
            .willReturn(step6);

        given(step6.fetch())
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
    @DisplayName("월 수입 보고서에 필요한 합계 조회 - 0")
    void getMonthlyIncomeSum_success_0() {
        Integer intA = 0;

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
        given(queryFactory.select(any(NumberExpression.class)))
            .willReturn(step1);

        JPAQuery step2 = mock(JPAQuery.class);
        given(step1.from(any(EntityPath.class)))
            .willReturn(step2);

        JPAQuery step3 = mock(JPAQuery.class);
        given(step2.where(
            any(BooleanExpression.class)))
            .willReturn(step3);

        given(step3.fetchOne())
            .willReturn(intA);

        //when
        int sum0
            = incomeQueryRepository.getMonthlyIncomeSum(1L,
            LocalDate.of(2022,10,1),
            LocalDate.of(2022,10,31));

        //then
        assertEquals(sum0, 0);
    }

    @Test
    @DisplayName("월 수입 보고서에 필요한 합계 조회 - int")
    void getMonthlyIncomeSum_success_int() {
        Integer intA = 10000;

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
        given(queryFactory.select(any(NumberExpression.class)))
            .willReturn(step1);

        JPAQuery step2 = mock(JPAQuery.class);
        given(step1.from(any(EntityPath.class)))
            .willReturn(step2);

        JPAQuery step3 = mock(JPAQuery.class);
        given(step2.where(
            any(BooleanExpression.class)))
            .willReturn(step3);

        given(step3.fetchOne())
            .willReturn(intA);

        //when
        int sum
            = incomeQueryRepository.getMonthlyIncomeSum(1L,
            LocalDate.of(2022,10,1),
            LocalDate.of(2022,10,31));

        //then
        assertEquals(sum, 10000);
    }
}