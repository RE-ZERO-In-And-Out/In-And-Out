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
import com.rezero.inandout.expense.model.ExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseDto;
import com.rezero.inandout.expense.model.ExpenseInput;
import com.rezero.inandout.expense.repository.DetailExpenseCategoryRepository;
import com.rezero.inandout.expense.repository.ExpenseCategoryRepository;
import com.rezero.inandout.expense.repository.ExpenseRepository;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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
    private ExpenseCategoryRepository expenseCategoryRepository;

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

    @Nested
    class getExpensesMethod {

        Member member = Member.builder()
            .memberId(1L)
            .email("hgd@gmail.com")
            .password("1234")
            .build();

        @Test
        void getExpenses_success() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

            DetailExpenseCategory detailExpenseCategory =
                DetailExpenseCategory.builder()
                    .detailExpenseCategoryId(1L)
                    .detailExpenseCategoryName("간식")
                    .build();

            List<Expense> expenses =
                Arrays.asList(
                    Expense.builder()
                        .expenseId(1L)
                        .member(member)
                        .expenseDt(LocalDate.of(2020,10,20))
                        .expenseItem("초코틴틴")
                        .expenseCash(0)
                        .expenseCard(1200)
                        .detailExpenseCategory(detailExpenseCategory)
                        .expenseMemo("냠냠")
                        .build()
                );

            given(expenseRepository.findAllByMemberAndExpenseDtBetween(any(), any(), any()))
                .willReturn(expenses);

            //when
            List<ExpenseDto> expenseDtos = expenseServiceImpl
                .getExpenses("hgd@gmail.com",
                    LocalDate.of(2020, 10, 1),
                    LocalDate.of(2020, 10, 31));

            //then
            assertEquals(expenseDtos.get(0).getExpenseId(), 1L);
            assertEquals(expenseDtos.get(0).getExpenseDt(), LocalDate.of(2020,10,20));
            assertEquals(expenseDtos.get(0).getExpenseItem(), "초코틴틴");
            assertEquals(expenseDtos.get(0).getExpenseCash(), 0);
            assertEquals(expenseDtos.get(0).getExpenseCard(), 1200);
            assertEquals(expenseDtos.get(0).getDetailExpenseCategoryDto().getDetailExpenseCategoryName(), "간식");
            assertEquals(expenseDtos.get(0).getExpenseMemo(), "냠냠");
        }

        @Test
        void getExpenses_fail_notFoundMember() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

            //when
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> expenseServiceImpl.getExpenses(anyString(),
                    LocalDate.of(2020, 10, 1),
                    LocalDate.of(2020, 10, 31)));

            //then
            assertEquals(exception.getMessage(), "계정을 찾을 수 없습니다.");
        }
    }

    @Test
    void getExpenseCategories_success() {
        //given
        ExpenseCategory expenseCategory =
            ExpenseCategory.builder()
                .expenseCategoryId(1L)
                .expenseCategoryName("식비")
                .build();

        List<ExpenseCategory> expenseCategories = Arrays.asList(expenseCategory);

        given(expenseCategoryRepository.findAll())
            .willReturn(expenseCategories);

        DetailExpenseCategory detailExpenseCategory =
            DetailExpenseCategory.builder()
                .detailExpenseCategoryId(1L)
                .detailExpenseCategoryName("간식")
                .expenseCategory(expenseCategory)
                .build();

        List<DetailExpenseCategory> detailExpenseCategories = Arrays.asList(
            detailExpenseCategory
        );

        given(detailExpenseCategoryRepository.findAllByExpenseCategory(expenseCategory))
            .willReturn(detailExpenseCategories);

        //when
        List<ExpenseCategoryDto> expenseCategoryDtos = expenseServiceImpl.getExpenseCategories();

        //then
        assertEquals(expenseCategoryDtos.get(0).getExpenseCategoryId(), 1L);
        assertEquals(expenseCategoryDtos.get(0).getExpenseCategoryName(), "식비");
        assertEquals(expenseCategoryDtos.get(0)
            .getDetailExpenseCategoryDtos().get(0).getDetailExpenseCategoryId(), 1L);
        assertEquals(expenseCategoryDtos.get(0)
            .getDetailExpenseCategoryDtos().get(0).getDetailExpenseCategoryName(), "간식");
    }
}