package com.rezero.inandout.income.service.base;


import com.rezero.inandout.calendar.model.CalendarIncomeDto;
import com.rezero.inandout.income.model.DeleteIncomeInput;
import com.rezero.inandout.income.model.DetailIncomeCategoryDto;
import com.rezero.inandout.income.model.IncomeCategoryDto;
import com.rezero.inandout.income.model.IncomeDto;
import com.rezero.inandout.income.model.IncomeInput;
import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.model.YearlyIncomeReportDto;
import java.time.LocalDate;
import java.util.List;

public interface IncomeService {

    void addIncome(String email, List<IncomeInput> incomeInputList);
    List<IncomeDto> getIncomeList(String email, LocalDate startDt, LocalDate endDt);
    List<IncomeCategoryDto> getIncomeCategoryList();
    List<DetailIncomeCategoryDto> getDetailIncomeCategory();
    void updateIncome(String email, List<IncomeInput> incomeInputList);
    void deleteIncome(String email, List<DeleteIncomeInput> deleteIncomeInputList);


    List<ReportDto> getMonthlyIncomeReport(String email, LocalDate startDt, LocalDate endDt);
    List<YearlyIncomeReportDto> getYearlyIncomeReport(String email, LocalDate startDt, LocalDate endDt);


    List<CalendarIncomeDto> getMonthlyIncomeCalendar(String email, LocalDate startDt, LocalDate endDt);

}
