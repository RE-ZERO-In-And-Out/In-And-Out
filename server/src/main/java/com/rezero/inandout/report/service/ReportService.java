package com.rezero.inandout.report.service;

import com.rezero.inandout.report.model.ReportDto;

import com.rezero.inandout.report.model.YearlyReportDto;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    List<ReportDto> getMonthlyIncomeReport(String email, LocalDate startDt, LocalDate endDt);

    List<ReportDto> getMonthlyExpenseReport(String email, LocalDate startDt, LocalDate endDt);

    List<YearlyReportDto> getYearlyIncomeReport(String email, LocalDate startDt, LocalDate endDt);

}
