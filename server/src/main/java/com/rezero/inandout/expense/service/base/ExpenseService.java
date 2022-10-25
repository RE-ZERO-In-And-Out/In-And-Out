package com.rezero.inandout.expense.service.base;

import com.rezero.inandout.expense.model.DeleteExpenseInput;
import com.rezero.inandout.expense.model.ExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseDto;
import com.rezero.inandout.expense.model.ExpenseInput;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {

    void addExpense(String email, List<ExpenseInput> inputs);

    List<ExpenseDto> getExpenses(String email, LocalDate startDt, LocalDate endDt);

    List<ExpenseCategoryDto> getExpenseCategories();

    void updateExpense(String email, List<ExpenseInput> inputs);

    void deleteExpense(String email, List<DeleteExpenseInput> inputs);
}
