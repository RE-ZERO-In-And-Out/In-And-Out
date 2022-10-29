package com.rezero.inandout.expense.repository;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rezero.inandout.expense.entity.QExpense;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.report.model.ReportDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExpenseQueryRepository 테스트")
class ExpenseQueryRepositoryTest {

    @Mock
    private JPAQueryFactory jpaQueryFactory;

    @InjectMocks
    private ExpenseQueryRepository expenseQueryRepository;

    @Test
    void getExpenseMonthReport() {
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
                                .sum().multiply(100).doubleValue()
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

        given(resultStep4.fetch())
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
    void getTotalSum() {
        //given
        QExpense expense = QExpense.expense;

        Integer totalSum = 8800000 + 23000000 + 200000 + 41000 + 462000;

        JPAQuery totalSumStep1 = Mockito.mock(JPAQuery.class);
        given(jpaQueryFactory.select(expense.expenseCard.add(expense.expenseCash).sum()))
                .willReturn(totalSumStep1);

        JPAQuery totalSumStep2 = Mockito.mock(JPAQuery.class);
        given(totalSumStep1.from(any(EntityPath.class)))
                .willReturn(totalSumStep2);

        JPAQuery totalSumStep3 = Mockito.mock(JPAQuery.class);
        given(totalSumStep2.where(
                any(BooleanExpression.class),
                any(BooleanExpression.class)))
                .willReturn(totalSumStep3);

        given(totalSumStep3.fetchOne())
                .willReturn(totalSum);
        //when
        Integer result = expenseQueryRepository.getTotalSum(
                Member.builder()
                        .memberId(1L)
                        .email("hgd@gmail.com")
                        .password("1234")
                        .build(),
                LocalDate.of(2022, 10, 1),
                LocalDate.of(2022, 10, 31)
        );
        //then
        assertEquals(8800000 + 23000000 + 200000 + 41000 + 462000, result);
    }
}