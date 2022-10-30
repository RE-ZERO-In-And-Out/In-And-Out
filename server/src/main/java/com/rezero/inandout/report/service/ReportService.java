package com.rezero.inandout.report.service;

import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.model.YearlyExpenseReportDto;
import com.rezero.inandout.report.model.YearlyIncomeReportDto;
import com.rezero.inandout.report.model.YearlyTotalReportDto;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    List<ReportDto> getMonthlyIncomeReport(String email, LocalDate startDt, LocalDate endDt);

    List<ReportDto> getMonthlyExpenseReport(String email, LocalDate startDt, LocalDate endDt);

    List<YearlyExpenseReportDto> getYearlyExpenseReport(String email, LocalDate startDt, LocalDate endDt);

    List<YearlyIncomeReportDto> getYearlyIncomeReport(String email, LocalDate startDt, LocalDate endDt);

    YearlyTotalReportDto getYearlyTotalReport(String email, LocalDate startDt, LocalDate endDt);


}
