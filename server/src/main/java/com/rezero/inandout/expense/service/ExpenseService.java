package com.rezero.inandout.expense.service;

import com.rezero.inandout.expense.model.ExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseDto;
import com.rezero.inandout.expense.model.ExpenseInput;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {

    void addExpense(String name, List<ExpenseInput> inputs);

    List<ExpenseDto> getExpenses(String email, LocalDate startDt, LocalDate endDt);

    List<ExpenseCategoryDto> getExpenseCategories();
}
