package com.rezero.inandout.expense.repository;

import com.rezero.inandout.expense.entity.Expense;
import com.rezero.inandout.expense.entity.MonthlyExpenseReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyExpenseReportRepository extends JpaRepository<MonthlyExpenseReport, Long> {

}
