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

        JPAQuery step5 = mock(JPAQuery.class);
        given(queryFactory
            .select(Projections.constructor(ReportDto.class,
            incomeCategory.incomeCategoryName,
            income.incomeAmount.sum(),
            income.incomeAmount.sum()
                .multiply(100)
                .doubleValue()
                .divide(intA)
                .multiply(100)
                .round()
                .divide(100.0)
                )
            )
        ).willReturn(step5);

        JPAQuery step6 = mock(JPAQuery.class);
        given(step5.from(any(EntityPath.class)))
            .willReturn(step6);

        JPAQuery step7 = mock(JPAQuery.class);
        given(step6.leftJoin(any(EntityPath.class), any(EntityPath.class)))
            .willReturn(step7);

        JPAQuery step8 = mock(JPAQuery.class);
        given(step7.where(any(BooleanExpression.class)))
            .willReturn(step8);

        JPAQuery step9 = mock(JPAQuery.class);
        given(step8.groupBy(incomeCategory.incomeCategoryName))
            .willReturn(step9);

        JPAQuery step10 = mock(JPAQuery.class);
        given(step9.orderBy(income.incomeAmount.sum().desc()))
            .willReturn(step10);

        mock(JPAQuery.class);
        given(step10.fetch())
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
}