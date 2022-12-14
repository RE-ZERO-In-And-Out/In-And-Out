package com.rezero.inandout.expense.repository;

import com.rezero.inandout.expense.entity.Expense;
import com.rezero.inandout.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByMemberAndExpenseDtBetweenOrderByExpenseDt(Member member, LocalDate startDt, LocalDate endDt);

    Optional<Expense> findByExpenseId(Long expenseId);
}
