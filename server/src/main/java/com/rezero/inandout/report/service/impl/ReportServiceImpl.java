package com.rezero.inandout.report.service.impl;


import com.rezero.inandout.expense.service.base.ExpenseService;
import com.rezero.inandout.exception.MemberException;
import com.rezero.inandout.exception.errorcode.MemberErrorCode;
import com.rezero.inandout.income.service.base.impl.IncomeServiceImpl;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.model.YearlyReportDto;
import com.rezero.inandout.report.repository.ExpenseQueryRepository;
import com.rezero.inandout.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final MemberRepository memberRepository;
    private final IncomeQueryRepository incomeQueryRepository;
    private final ExpenseQueryRepository expenseQueryRepository;
    private final ExpenseService expenseService;
    private final IncomeServiceImpl incomeService;

    @Override
    public List<ReportDto> getMonthlyIncomeReport(String email, LocalDate startDt, LocalDate endDt) {
        return incomeService.getMonthlyIncomeReport(email, startDt, endDt);
    }

    @Override
    public List<ReportDto> getMonthlyExpenseReport(String email, LocalDate startDt, LocalDate endDt) {

        return expenseService.getMonthlyExpenseReport(email, startDt, endDt);
    }

    @Override
    public List<YearlyReportDto> getYearlyExpenseReport(String email, LocalDate startDt, LocalDate endDt) {

        return expenseService.getYearlyExpenseReport(email, startDt, endDt);
    }

    @Override
    public List<YearlyReportDto> getYearlyIncomeReport(String email, LocalDate startDt,
                                                       LocalDate endDt) {
        return incomeService.getYearlyIncomeReport(email, startDt, endDt);
    }
}