package com.rezero.inandout.expense.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rezero.inandout.expense.entity.QExpense;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.report.model.ReportDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ExpenseQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<ReportDto> getMonthlyExpenseReport(Member member, LocalDate startDt, LocalDate endDt) {

        QExpense expense = QExpense.expense;

        List<ReportDto> result = jpaQueryFactory
                .select(Projections.constructor(
                                        ReportDto.class,
                                        expense.detailExpenseCategory.expenseCategory
                                                .expenseCategoryName,
                                        expense.expenseCard.add(expense.expenseCash)
                                                .sum(),
                                        expense.expenseCard.add(expense.expenseCash)
                                                .sum().multiply(100).doubleValue()

                        )
                )
                .from(expense)
                .where(expense.member.eq(member),
                        expense.expenseDt.between(startDt, endDt))
                .groupBy(expense.detailExpenseCategory.expenseCategory.expenseCategoryName)
                .fetch();

        return result;
    }

    public Integer getTotalSum(Member member, LocalDate startDt, LocalDate endDt) {

        QExpense expense = QExpense.expense;

        return jpaQueryFactory.select(expense.expenseCard.add(expense.expenseCash).sum())
                .from(expense)
                .where(expense.member.eq(member),
                        expense.expenseDt.between(startDt, endDt))
                .fetchOne();
    }

}
