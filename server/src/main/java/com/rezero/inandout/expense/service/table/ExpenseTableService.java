package com.rezero.inandout.expense.service.table;

import com.rezero.inandout.expense.model.CategoryAndExpenseDto;
import com.rezero.inandout.expense.model.ExpenseInput;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseTableService {
    void addAndUpdateExpense(String email, List<ExpenseInput> inputs);

    CategoryAndExpenseDto getCategoryAndExpenseDto(String email, LocalDate startDt, LocalDate endDt);
}
