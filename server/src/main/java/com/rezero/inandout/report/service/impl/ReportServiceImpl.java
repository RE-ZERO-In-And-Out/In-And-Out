package com.rezero.inandout.report.service.impl;


import com.rezero.inandout.expense.service.base.ExpenseService;
import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.model.YearlyReportDto;
import com.rezero.inandout.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ExpenseService expenseService;

    @Override
    public List<ReportDto> getMonthlyIncomeReport(String email, LocalDate startDt, LocalDate endDt) {

        return null;
    }

    @Override
    public List<ReportDto> getMonthlyExpenseReport(String email, LocalDate startDt, LocalDate endDt) {

        return expenseService.getMonthlyExpenseReport(email, startDt, endDt);
    }

    @Override
    public List<YearlyReportDto> getYearlyExpenseReport(String email, LocalDate startDt, LocalDate endDt) {

        return expenseService.getYearlyExpenseReport(email, startDt, endDt);
    }
}