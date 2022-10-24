package com.rezero.inandout.expense.service.impl;

import com.rezero.inandout.exception.ExpenseException;
import com.rezero.inandout.exception.errorcode.ExpenseErrorCode;
import com.rezero.inandout.expense.entity.DetailExpenseCategory;
import com.rezero.inandout.expense.entity.Expense;
import com.rezero.inandout.expense.entity.ExpenseCategory;
import com.rezero.inandout.expense.model.*;
import com.rezero.inandout.expense.repository.DetailExpenseCategoryRepository;
import com.rezero.inandout.expense.repository.ExpenseCategoryRepository;
import com.rezero.inandout.expense.repository.ExpenseRepository;
import com.rezero.inandout.expense.service.ExpenseService;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

        List<Expense> expenses = expenseRepository.findAllByMemberAndExpenseDtBetweenOrderByExpenseDt(member, startDt, endDt);

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

    @Override
    @Transactional
    public void updateExpense(String email, List<ExpenseInput> inputs) {
        Member member = findMemberByEmail(email);

        List<Expense> expenses = new ArrayList<>();

        for (ExpenseInput input : inputs) {
            Expense expense = findExpenseByExpenseId(input.getExpenseId());
            validateMatchingMemberAndExpense(expense.getExpenseId(), member);

            expense.setExpenseDt(input.getExpenseDt());
            expense.setExpenseItem(input.getExpenseItem());
            expense.setExpenseCash(input.getExpenseCash());
            expense.setExpenseCard(input.getExpenseCard());
            expense.setDetailExpenseCategory(
                findDetailExpenseCategoryById(
                    input.getDetailExpenseCategoryId()
                )
            );
            expense.setExpenseMemo(input.getExpenseMemo());

            expenses.add(expense);
        }

        expenseRepository.saveAll(expenses);
    }

    @Override
    @Transactional
    public void deleteExpense(String email, List<DeleteExpenseInput> inputs) {
        Member member = findMemberByEmail(email);

        List<Long> expenseIds = new ArrayList<>();

        for (DeleteExpenseInput input : inputs) {
            Expense expense = findExpenseByExpenseId(input.getExpenseId());
            validateMatchingMemberAndExpense(expense.getExpenseId(), member);
            expenseIds.add(input.getExpenseId());
        }

        expenseRepository.deleteAllByIdInBatch(expenseIds);
    }

    private Expense findExpenseByExpenseId(Long expenseId) {
        return expenseRepository.findByExpenseId(expenseId)
                .orElseThrow(() -> new ExpenseException(ExpenseErrorCode.NO_EXPENSE));
    }

    private void validateMatchingMemberAndExpense(Long expenseId, Member member) {
        Expense expense = findExpenseByExpenseId(expenseId);
        Long expenseMemberId = expense.getMember().getMemberId();

        if (!expenseMemberId.equals(member.getMemberId())) {
            throw new ExpenseException(ExpenseErrorCode.NOT_MATCH_MEMBER_AND_EXPENSE);
        }
    }


    private Member findMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ExpenseException(ExpenseErrorCode.NO_MEMBER));

        return member;
    }

    private DetailExpenseCategory findDetailExpenseCategoryById(Long detailExpenseCategoryId) {
        return detailExpenseCategoryRepository
            .findByDetailExpenseCategoryId(detailExpenseCategoryId)
            .orElseThrow(() -> new ExpenseException(ExpenseErrorCode.NO_CATEGORY));
    }
}
