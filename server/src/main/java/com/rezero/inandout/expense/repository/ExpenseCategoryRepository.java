package com.rezero.inandout.expense.repository;

import com.rezero.inandout.expense.entity.Expense;
import com.rezero.inandout.expense.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {

}
