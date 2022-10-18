package com.rezero.inandout.expense.service;

import com.rezero.inandout.expense.model.ExpenseInput;
import java.util.List;

public interface ExpenseService {

    void addExpense(String name, List<ExpenseInput> inputs);
}
