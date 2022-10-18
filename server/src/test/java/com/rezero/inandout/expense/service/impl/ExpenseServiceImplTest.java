package com.rezero.inandout.expense.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.rezero.inandout.expense.entity.DetailExpenseCategory;
import com.rezero.inandout.expense.entity.Expense;
import com.rezero.inandout.expense.entity.ExpenseCategory;
import com.rezero.inandout.expense.model.ExpenseInput;
import com.rezero.inandout.expense.repository.DetailExpenseCategoryRepository;
import com.rezero.inandout.expense.repository.ExpenseRepository;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private DetailExpenseCategoryRepository detailExpenseCategoryRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseServiceImpl;

    @Nested
    class addExpenseMethod {
        Member member = Member.builder()
            .memberId(1L)
            .email("hgd@gmail.com")
            .password("1234")
            .build();

        ExpenseInput input = ExpenseInput.builder()
            .detailExpenseCategoryId(1L)
            .expenseDt(LocalDate.now())
            .expenseItem("롤케익")
            .expenseCash(3000)
            .expenseCard(0)
            .expenseMemo("냠냠")
            .build();

        DetailExpenseCategory detailExpenseCategory =
            DetailExpenseCategory.builder()
                .detailExpenseCategoryId(1L)
                .detailExpenseCategoryName("간식")
                .expenseCategory(new ExpenseCategory())
                .build();

        Expense expense = Expense.builder()
            .member(member)
            .detailExpenseCategory(detailExpenseCategory)
            .expenseDt(input.getExpenseDt())
            .expenseCash(input.getExpenseCash())
            .expenseCard(input.getExpenseCard())
            .expenseMemo(input.getExpenseMemo())
            .build();

        @Test
        void addExpense_success() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

            given(detailExpenseCategoryRepository.findByDetailExpenseCategoryId(anyLong()))
                .willReturn(Optional.of(detailExpenseCategory));

            //when
            expenseServiceImpl.addExpense("hgd@gmail.com", Arrays.asList(input));

            //then
            verify(expenseRepository, times(1)).saveAll(any());
        }

        @Test
        void addExpense_fail_notFoundMember() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

            //when
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> expenseServiceImpl.addExpense("hgd@gmail.com", Arrays.asList(input)));

            //then
            assertEquals("계정을 찾을 수 없습니다.", exception.getMessage());

        }

        @Test
        void addExpense_fail_notFoundCategory() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

            given(detailExpenseCategoryRepository.findByDetailExpenseCategoryId(anyLong()))
                .willReturn(Optional.empty());

            //when
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> expenseServiceImpl.addExpense("hgd@gmail.com", Arrays.asList(input)));

            //then
            assertEquals("없는 카테고리 입니다.", exception.getMessage());
        }
    }
}