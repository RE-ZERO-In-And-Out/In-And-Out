package com.rezero.inandout.report.service.impl;


import com.rezero.inandout.expense.service.base.ExpenseService;
import com.rezero.inandout.income.service.base.impl.IncomeServiceImpl;
import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.model.YearlyExpenseReportDto;
import com.rezero.inandout.report.model.YearlyIncomeReportDto;
import com.rezero.inandout.report.model.YearlyTotalReportDto;
import com.rezero.inandout.report.service.ReportService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
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
    public List<YearlyExpenseReportDto> getYearlyExpenseReport(String email, LocalDate startDt, LocalDate endDt) {
        return expenseService.getYearlyExpenseReport(email, startDt, endDt);
    }

    @Override
    public List<YearlyIncomeReportDto> getYearlyIncomeReport(String email, LocalDate startDt, LocalDate endDt) {
        return incomeService.getYearlyIncomeReport(email, startDt, endDt);
    }

    @Override
    @Transactional
    public YearlyTotalReportDto getYearlyTotalReport(String email, LocalDate startDt, LocalDate endDt) {

        return YearlyTotalReportDto.builder()
                .incomeReportList(incomeService.getYearlyIncomeReport(email, startDt, endDt))
                .expenseReportList(expenseService.getYearlyExpenseReport(email, startDt, endDt))
                .build();
    }
}