package com.rezero.inandout.income.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rezero.inandout.income.entity.QDetailIncomeCategory;
import com.rezero.inandout.income.entity.QIncome;
import com.rezero.inandout.income.entity.QIncomeCategory;
import com.rezero.inandout.member.entity.QMember;
import com.rezero.inandout.report.model.ReportDto;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IncomeQueryRepository {

    private final JPAQueryFactory queryFactory;
    QMember member = new QMember("m");

    QIncome income = new QIncome("i");
    QIncomeCategory incomeCategory = new QIncomeCategory("ic");
    QDetailIncomeCategory detailIncomeCategory = new QDetailIncomeCategory("dic");

    public List<ReportDto> getMonthlyIncomeReport (Long id, LocalDate startDt, LocalDate endDt) {

        return queryFactory
            .select(Projections.constructor(ReportDto.class,
                incomeCategory.incomeCategoryName,
                income.incomeAmount.sum(),
                income.incomeAmount.sum()
                    .multiply(100).doubleValue()
                )
            )
            .from(income)
                .leftJoin(income.detailIncomeCategory.incomeCategory, incomeCategory)
            .where(income.member.memberId.eq(id)
                .and(income.incomeDt.between(startDt, endDt))
            )
            .groupBy(incomeCategory.incomeCategoryName)
            .orderBy(income.incomeAmount.sum().desc())
            .fetch();

    }


    public int getMonthlyIncomeSum(Long id, LocalDate startDt, LocalDate endDt) {
        Integer sum = queryFactory
            .select(income.incomeAmount.sum())
            .from(income)
            .where(income.member.memberId.eq(id)
                .and(income.incomeDt.between(startDt, endDt)))
            .fetchOne();

        return (sum == 0) ? 0 : sum;

    }

}