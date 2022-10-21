package com.rezero.inandout.expense.repository;

import com.rezero.inandout.expense.entity.Expense;
import com.rezero.inandout.member.entity.Member;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByMemberAndExpenseDtBetween(Member member, LocalDate startDt, LocalDate endDt);

    Optional<Expense> findByExpenseIdAndMember(Long expenseId, Member member);
}
