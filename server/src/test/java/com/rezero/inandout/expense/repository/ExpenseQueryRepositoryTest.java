package com.rezero.inandout.expense.repository;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rezero.inandout.calendar.model.CalendarExpenseDto;
import com.rezero.inandout.expense.entity.QExpense;
import com.rezero.inandout.member.entity.Member;
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
@DisplayName("ExpenseQueryRepository 테스트")
class ExpenseQueryRepositoryTest {

    @Mock
    private JPAQueryFactory jpaQueryFactory;

    @InjectMocks
    private ExpenseQueryRepository expenseQueryRepository;

    @Test
    void getMonthlyExpenseReport() {
        //given
        QExpense expense = QExpense.expense;

        List<ReportDto> result = Arrays.asList(
                new ReportDto("건강/문화", 8800000, 27.07),
                new ReportDto("교통/차량", 23000000, 70.76),
                new ReportDto("세금/이자", 200000, 0.62),
                new ReportDto("식비", 41000, 0.13),
                new ReportDto("의복/미용", 462000, 1.42)
        );

        JPAQuery resultStep1 = Mockito.mock(JPAQuery.class);
        given(jpaQueryFactory
                .select(Projections.constructor(
                        ReportDto.class,
                        expense.detailExpenseCategory.expenseCategory
                                .expenseCategoryName,
                        expense.expenseCard.add(expense.expenseCash)
                                .sum(),
                        expense.expenseCard.add(expense.expenseCash)
                                .sum().doubleValue()
                ))
        ).willReturn(resultStep1);

        JPAQuery resultStep2 = Mockito.mock(JPAQuery.class);
        given(resultStep1.from(any(EntityPath.class)))
                .willReturn(resultStep2);

        JPAQuery resultStep3 = Mockito.mock(JPAQuery.class);
        given(resultStep2.where(any(BooleanExpression.class),
                any(BooleanExpression.class))
        ).willReturn(resultStep3);

        JPAQuery resultStep4 = Mockito.mock(JPAQuery.class);
        given(resultStep3.groupBy(expense.detailExpenseCategory
                .expenseCategory.expenseCategoryName)
        ).willReturn(resultStep4);

        JPAQuery resultStep5 = Mockito.mock(JPAQuery.class);
        given(resultStep4.orderBy(expense.expenseDt.asc())
        ).willReturn(resultStep5);

        JPAQuery resultStep6 = Mockito.mock(JPAQuery.class);
        given(resultStep5.orderBy(expense.expenseCard.add(expense.expenseCash).sum().desc()))
                .willReturn(resultStep6);

        given(resultStep6.fetch())
                .willReturn(result);

        //when
        List<ReportDto> reportDtos = expenseQueryRepository
                .getMonthlyExpenseReport(
                        Member.builder()
                                .memberId(1L)
                                .email("hgd@gmail.com")
                                .password("1234")
                                .build(),
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
    void getYearlyExpenseReport() {
        //given
        QExpense expense = QExpense.expense;
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
        given(jpaQueryFactory.select(
                Projections.constructor(
                        YearlyReportDto.class,
                        expense.expenseDt.year(),
                        expense.expenseDt.month(),
                        expense.detailExpenseCategory.expenseCategory.expenseCategoryName,
                        expense.expenseCard.add(expense.expenseCash).sum(),
                        expense.expenseCard.add(expense.expenseCash).sum().doubleValue()
                ))
        ).willReturn(resultStep1);

        JPAQuery resultStep2 = Mockito.mock(JPAQuery.class);
        given(resultStep1.from(any(EntityPath.class)))
                .willReturn(resultStep2);

        JPAQuery resultStep3 = Mockito.mock(JPAQuery.class);
        given(resultStep2.where(any(BooleanExpression.class),
                any(BooleanExpression.class))).willReturn(resultStep3);

        JPAQuery resultStep4 = Mockito.mock(JPAQuery.class);
        given(resultStep3.groupBy(expense.expenseDt.month(),
                expense.detailExpenseCategory.expenseCategory.expenseCategoryName))
                .willReturn(resultStep4);

        JPAQuery resultStep5 = Mockito.mock(JPAQuery.class);
        given(resultStep4.orderBy(expense.expenseDt.year().asc(),
                expense.expenseDt.month().asc(),
                expense.expenseCard.add(expense.expenseCash).sum().desc()))
                .willReturn(resultStep5);

        given(resultStep5.fetch())
                .willReturn(result);

        //when
        List<YearlyReportDto> yearlyReportDtos = expenseQueryRepository
                .getYearlyExpenseReport(
                        Member.builder()
                                .memberId(1L)
                                .email("hgd@gmail.com")
                                .password("1234")
                                .build(),
                        LocalDate.of(2022, 1, 1),
                        LocalDate.of(2022, 3, 31)
                );

        //then
        assertEquals(yearlyReportDtos.get(0).getCategorySum(), 5000);
        assertEquals(yearlyReportDtos.get(1).getCategorySum(), 6000);
        assertEquals(yearlyReportDtos.get(2).getCategorySum(), 7000);
    }

    @Test
    @DisplayName("달력 월 지출내역 조회")
    void getMonthlyExpenseCalendar_success() {

        //given
        QExpense expense = QExpense.expense;

        List<CalendarExpenseDto> calendarExpenseDtoList = new ArrayList<>(Arrays.asList(
            CalendarExpenseDto.builder().expenseDt(LocalDate.of(2022, 10, 2))
                .item("지출1").amount(6543).build(),
            CalendarExpenseDto.builder().expenseDt(LocalDate.of(2022, 10, 28))
                .item("지출2").amount(12345).build()
        ));

        JPAQuery step1 = mock(JPAQuery.class);
        given(jpaQueryFactory.select(
                Projections.constructor(CalendarExpenseDto.class,
                    expense.expenseDt, expense.expenseItem,
                    expense.expenseCard.add(expense.expenseCash))))
            .willReturn(step1);

        JPAQuery step2 = mock(JPAQuery.class);
        given(step1.from(expense))
            .willReturn(step2);

        JPAQuery step3 = mock(JPAQuery.class);
        given(step2.where(
            any(BooleanExpression.class)))
            .willReturn(step3);

        JPAQuery step4 = mock(JPAQuery.class);
        given(step3.orderBy(
            expense.expenseDt.asc()))
            .willReturn(step4);

        given(step4.fetch())
            .willReturn(calendarExpenseDtoList);

        //when
        List<CalendarExpenseDto> getCalendarExpenseDtoList
            = expenseQueryRepository.getMonthlyExpenseCalendar(1L,
            LocalDate.of(2022,10,1),
            LocalDate.of(2022,10,31));

        //then
        assertEquals(getCalendarExpenseDtoList, calendarExpenseDtoList);
    }
}