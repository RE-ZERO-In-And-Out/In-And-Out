package com.rezero.inandout.expense.service.impl;

import com.rezero.inandout.expense.entity.DetailExpenseCategory;
import com.rezero.inandout.expense.entity.Expense;
import com.rezero.inandout.expense.entity.ExpenseCategory;
import com.rezero.inandout.expense.model.DeleteExpenseInput;
import com.rezero.inandout.expense.model.ExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseDto;
import com.rezero.inandout.expense.model.ExpenseInput;
import com.rezero.inandout.expense.repository.DetailExpenseCategoryRepository;
import com.rezero.inandout.expense.repository.ExpenseCategoryRepository;
import com.rezero.inandout.expense.repository.ExpenseRepository;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        @DisplayName("지출내역 추가 - 성공")
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
        @DisplayName("지출내역 추가 - 실패 : 계정 없음")
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
        @DisplayName("지출내역 추가 - 실패 : 카테고리 없음")
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
        @DisplayName("지출내역 조회 - 성공")
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
            assertEquals(expenseDtos.get(0).getDetailExpenseCategoryId(), 1L);
            assertEquals(expenseDtos.get(0).getExpenseMemo(), "냠냠");
        }

        @Test
        @DisplayName("지출내역 조회 - 실패 : 계정 없음")
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
    @DisplayName("카테고리 조회 - 성공")
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

    @Nested
    class updateExpenseMethod {
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
            .expenseId(1L)
            .member(member)
            .detailExpenseCategory(detailExpenseCategory)
            .expenseDt(input.getExpenseDt())
            .expenseCash(input.getExpenseCash())
            .expenseCard(input.getExpenseCard())
            .expenseMemo(input.getExpenseMemo())
            .build();

        @Test
        @DisplayName("지출내역 수정 - 성공")
        void updateExpense_success() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

            given(expenseRepository.findByExpenseIdAndMember(any(), any()))
                .willReturn(Optional.of(expense));

            given(detailExpenseCategoryRepository.findByDetailExpenseCategoryId(any()))
                .willReturn(Optional.of(detailExpenseCategory));

            //when
            expenseServiceImpl.updateExpense("hgd@gmail.com", Arrays.asList(input));

            //then
            verify(expenseRepository, times(1)).saveAll(any());
        }

        @Test
        @DisplayName("지출내역 수정 - 실패 : 계정 없음")
        void updateExpense_fail_notFoundMember() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());
            //when
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> expenseServiceImpl.updateExpense("hgd@gmail.com", Arrays.asList(input)));
            //then
            assertEquals(exception.getMessage(), "계정을 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("지출내역 수정 - 실패 : 지출내역 없음")
        void updateExpense_fail_notFoundExpense() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

            given(expenseRepository.findByExpenseIdAndMember(any(), any()))
                .willReturn(Optional.empty());

            //when
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> expenseServiceImpl.updateExpense("hgd@gmail.com", Arrays.asList(input)));
            //then
            assertEquals(exception.getMessage(), "없는 지출 내역입니다.");
        }

        @Test
        @DisplayName("지출내역 수정 - 실패 : 카테고리 없음")
        void updateExpense_fail_notFoundCategory() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

            given(expenseRepository.findByExpenseIdAndMember(any(), any()))
                .willReturn(Optional.of(expense));

            given(detailExpenseCategoryRepository.findByDetailExpenseCategoryId(any()))
                .willReturn(Optional.empty());

            //when
            RuntimeException exception = assertThrows(RuntimeException.class,
                () -> expenseServiceImpl.updateExpense("hgd@gmail.com", Arrays.asList(input)));
            //then
            assertEquals(exception.getMessage(), "없는 카테고리 입니다.");
        }
    }

    @Nested
    class deleteExpenseMethod {
        Member member = Member.builder()
                .memberId(1L)
                .email("hgd@gmail.com")
                .password("1234")
                .build();

        List<DeleteExpenseInput> list = Arrays.asList(
                new DeleteExpenseInput(1L)
        );

        @Test
        @DisplayName("지출내역 삭제 - 성공")
        void updateExpense_success() {
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(member));

            //when
            expenseServiceImpl.deleteExpense("hgd@gmail.com", list);

            //then
            verify(expenseRepository, times(1)).deleteAllByExpenseIdInBatch(any());
        }

        @Test
        @DisplayName("지출내역 삭제 - 실패 : 계정 없음")
        void updateExpense_fail_notFoundUser() {
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.empty());

            //when
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> expenseServiceImpl.deleteExpense("hgd@gmail.com", list));

            //then
            assertEquals("계정을 찾을 수 없습니다.", exception.getMessage());
        }
    }
}