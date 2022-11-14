package com.rezero.inandout.expense.service.table.impl;

import com.rezero.inandout.expense.model.CategoryAndExpenseDto;
import com.rezero.inandout.expense.model.ExpenseInput;
import com.rezero.inandout.expense.service.table.ExpenseTableService;
import com.rezero.inandout.expense.service.base.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseTableServiceImpl implements ExpenseTableService {

    private final ExpenseService expenseService;

    @Override
    @Transactional
    public void addAndUpdateExpense(String email, List<ExpenseInput> inputs) {

        List<ExpenseInput> addExpenseInputs = new ArrayList<>();
        List<ExpenseInput> updateExpenseInputs = new ArrayList<>();

        for (ExpenseInput expenseInput : inputs) {
            if (expenseInput.getExpenseId() != null) {
                updateExpenseInputs.add(expenseInput);
            } else {
                addExpenseInputs.add(expenseInput);
            }
        }

        expenseService.updateExpense(email, updateExpenseInputs);
        expenseService.addExpense(email, addExpenseInputs);

    }

    @Override
    @Transactional
    public CategoryAndExpenseDto getCategoryAndExpenseDto(String email, LocalDate startDt, LocalDate endDt) {
        return CategoryAndExpenseDto.builder()
                .expenseCategoryDtos(expenseService.getExpenseCategories())
                .expenseDtos(expenseService.getExpenses(email, startDt, endDt))
                .build();
    }
}
