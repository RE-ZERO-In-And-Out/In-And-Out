package com.rezero.inandout.expense.service;

import com.rezero.inandout.expense.model.ExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseDto;
import com.rezero.inandout.expense.model.ExpenseInput;
import com.rezero.inandout.member.entity.Member;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {

    void addExpense(String email, List<ExpenseInput> inputs);

    List<ExpenseDto> getExpenses(String email, LocalDate startDt, LocalDate endDt);

    List<ExpenseCategoryDto> getExpenseCategories();

    void updateExpense(String email, List<ExpenseInput> inputs);
}
