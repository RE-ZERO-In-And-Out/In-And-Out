package com.rezero.inandout.expense.service.impl;

import com.rezero.inandout.expense.entity.DetailExpenseCategory;
import com.rezero.inandout.expense.entity.Expense;
import com.rezero.inandout.expense.entity.ExpenseCategory;
import com.rezero.inandout.expense.model.DetailExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseDto;
import com.rezero.inandout.expense.model.ExpenseInput;
import com.rezero.inandout.expense.repository.DetailExpenseCategoryRepository;
import com.rezero.inandout.expense.repository.ExpenseCategoryRepository;
import com.rezero.inandout.expense.repository.ExpenseRepository;
import com.rezero.inandout.expense.service.ExpenseService;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final DetailExpenseCategoryRepository detailExpenseCategoryRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void addExpense(String email, List<ExpenseInput> inputs) {
        Member member = findMemberByEmail(email);

        List<Expense> expenses = new ArrayList<>();

        for (ExpenseInput input : inputs) {

            DetailExpenseCategory detailExpenseCategory = findDetailExpenseCategoryById(
                input.getDetailExpenseCategoryId());

            expenses.add(
                Expense.builder()
                .member(member)
                .detailExpenseCategory(detailExpenseCategory)
                .expenseDt(input.getExpenseDt())
                .expenseItem(input.getExpenseItem())
                .expenseCash(input.getExpenseCash())
                .expenseCard(input.getExpenseCard())
                .expenseMemo(input.getExpenseMemo())
                .build()
            );

        }

        expenseRepository.saveAll(expenses);
    }

    @Override
    public List<ExpenseDto> getExpenses(String email, LocalDate startDt, LocalDate endDt) {
        Member member = findMemberByEmail(email);

        List<Expense> expenses = expenseRepository.findAllByMemberAndExpenseDtBetween(member, startDt, endDt);

        List<ExpenseDto> expenseDtos = ExpenseDto.toDtos(expenses);

        return expenseDtos;
    }

    @Override
    public List<ExpenseCategoryDto> getExpenseCategories() {
        List<ExpenseCategory> expenseCategories = expenseCategoryRepository.findAll();

        List<ExpenseCategoryDto> expenseCategoryDtos = new ArrayList<>();

        for (ExpenseCategory expenseCategory : expenseCategories) {
            ExpenseCategoryDto expenseCategoryDto = ExpenseCategoryDto.toDto(expenseCategory);

            List<DetailExpenseCategory> detailExpenseCategories =
                detailExpenseCategoryRepository.findAllByExpenseCategory(expenseCategory);

            expenseCategoryDto.setDetailExpenseCategoryDtos(DetailExpenseCategoryDto.toDtos(detailExpenseCategories));

            expenseCategoryDtos.add(expenseCategoryDto);
        }

        return expenseCategoryDtos;
    }


    private Member findMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("계정을 찾을 수 없습니다."));

        return member;
    }

    private DetailExpenseCategory findDetailExpenseCategoryById(Long detailExpenseCategoryId) {
        return detailExpenseCategoryRepository
            .findByDetailExpenseCategoryId(detailExpenseCategoryId)
            .orElseThrow(() -> new RuntimeException("없는 카테고리 입니다."));
    }
}
