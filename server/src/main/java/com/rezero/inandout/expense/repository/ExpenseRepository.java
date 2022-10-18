package com.rezero.inandout.expense.repository;

import com.rezero.inandout.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

}
