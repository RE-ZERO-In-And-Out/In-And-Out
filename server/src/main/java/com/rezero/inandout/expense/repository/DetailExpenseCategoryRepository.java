package com.rezero.inandout.expense.repository;

import com.rezero.inandout.expense.entity.DetailExpenseCategory;
import com.rezero.inandout.expense.entity.Expense;
import com.rezero.inandout.expense.entity.ExpenseCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetailExpenseCategoryRepository extends JpaRepository<DetailExpenseCategory, Long> {

    Optional<DetailExpenseCategory> findByDetailExpenseCategoryId(Long detailExpenseCategoryId);

    List<DetailExpenseCategory> findAllByExpenseCategory(ExpenseCategory expenseCategory);
}
