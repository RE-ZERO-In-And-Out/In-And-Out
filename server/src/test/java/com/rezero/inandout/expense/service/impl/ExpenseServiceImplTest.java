package com.rezero.inandout.expense.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.rezero.inandout.calendar.model.CalendarExpenseDto;
import com.rezero.inandout.exception.ExpenseException;
import com.rezero.inandout.exception.errorcode.ExpenseErrorCode;
import com.rezero.inandout.expense.entity.DetailExpenseCategory;
import com.rezero.inandout.expense.entity.Expense;
import com.rezero.inandout.expense.entity.ExpenseCategory;
import com.rezero.inandout.expense.model.DeleteExpenseInput;
import com.rezero.inandout.expense.model.ExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseDto;
import com.rezero.inandout.expense.model.ExpenseInput;
import com.rezero.inandout.expense.repository.DetailExpenseCategoryRepository;
import com.rezero.inandout.expense.repository.ExpenseCategoryRepository;
import com.rezero.inandout.expense.repository.ExpenseQueryRepository;
import com.rezero.inandout.expense.repository.ExpenseRepository;
import com.rezero.inandout.expense.service.base.impl.ExpenseServiceImpl;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.redis.RedisService;
import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.model.YearlyExpenseReportDto;
import com.rezero.inandout.report.model.YearlyReportDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExpenseServiceImpl ?????????")
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private DetailExpenseCategoryRepository detailExpenseCategoryRepository;

    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ExpenseQueryRepository expenseQueryRepository;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private ExpenseServiceImpl expenseServiceImpl;

    @Nested
    @DisplayName("???????????? ??????")
    class addExpenseMethod {
        Member member = Member.builder()
            .memberId(1L)
            .email("hgd@gmail.com")
            .password("1234")
            .build();

        ExpenseInput input = ExpenseInput.builder()
            .detailExpenseCategoryId(1L)
            .expenseDt(LocalDate.now())
            .expenseItem("?????????")
            .expenseCash(3000)
            .expenseCard(0)
            .expenseMemo("??????")
            .build();

        DetailExpenseCategory detailExpenseCategory =
            DetailExpenseCategory.builder()
                .detailExpenseCategoryId(1L)
                .detailExpenseCategoryName("??????")
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
        @DisplayName("???????????? ?????? - ??????")
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
        @DisplayName("???????????? ?????? - ?????? : ?????? ??????")
        void addExpense_fail_notFoundMember() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

            //when
            ExpenseException exception = assertThrows(ExpenseException.class,
                () -> expenseServiceImpl.addExpense("hgd@gmail.com", Arrays.asList(input)));

            //then
            assertEquals("?????? ???????????????.", exception.getErrorCode().getDescription());

        }

        @Test
        @DisplayName("???????????? ?????? - ?????? : ???????????? ??????")
        void addExpense_fail_notFoundCategory() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

            given(detailExpenseCategoryRepository.findByDetailExpenseCategoryId(anyLong()))
                .willReturn(Optional.empty());

            //when
            ExpenseException exception = assertThrows(ExpenseException.class,
                () -> expenseServiceImpl.addExpense("hgd@gmail.com", Arrays.asList(input)));

            //then
            assertEquals("?????? ???????????? ?????????.", exception.getErrorCode().getDescription());
        }
    }

    @Nested
    @DisplayName("???????????? ??????")
    class getExpensesMethod {

        Member member = Member.builder()
            .memberId(1L)
            .email("hgd@gmail.com")
            .password("1234")
            .build();

        @Test
        @DisplayName("???????????? ?????? - ??????")
        void getExpenses_success() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

            DetailExpenseCategory detailExpenseCategory =
                DetailExpenseCategory.builder()
                    .detailExpenseCategoryId(1L)
                    .detailExpenseCategoryName("??????")
                    .build();

            List<Expense> expenses =
                Arrays.asList(
                    Expense.builder()
                        .expenseId(1L)
                        .member(member)
                        .expenseDt(LocalDate.of(2020,10,20))
                        .expenseItem("????????????")
                        .expenseCash(0)
                        .expenseCard(1200)
                        .detailExpenseCategory(detailExpenseCategory)
                        .expenseMemo("??????")
                        .build()
                );

            given(expenseRepository.findAllByMemberAndExpenseDtBetweenOrderByExpenseDt(any(), any(), any()))
                .willReturn(expenses);

            //when
            List<ExpenseDto> expenseDtos = expenseServiceImpl
                .getExpenses("hgd@gmail.com",
                    LocalDate.of(2020, 10, 1),
                    LocalDate.of(2020, 10, 31));

            //then
            assertEquals(expenseDtos.get(0).getExpenseId(), 1L);
            assertEquals(expenseDtos.get(0).getExpenseDt(), LocalDate.of(2020,10,20));
            assertEquals(expenseDtos.get(0).getExpenseItem(), "????????????");
            assertEquals(expenseDtos.get(0).getExpenseCash(), 0);
            assertEquals(expenseDtos.get(0).getExpenseCard(), 1200);
            assertEquals(expenseDtos.get(0).getDetailExpenseCategoryId(), 1L);
            assertEquals(expenseDtos.get(0).getExpenseMemo(), "??????");
        }

        @Test
        @DisplayName("???????????? ?????? - ?????? : ?????? ??????")
        void getExpenses_fail_notFoundMember() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

            //when
            ExpenseException exception = assertThrows(ExpenseException.class,
                () -> expenseServiceImpl.getExpenses(anyString(),
                    LocalDate.of(2020, 10, 1),
                    LocalDate.of(2020, 10, 31)));

            //then
            assertEquals(exception.getErrorCode().getDescription(), "?????? ???????????????.");
        }
    }

    @Nested
    @DisplayName("?????? ???????????? ??????")
    class getExpenseCategories {
        ExpenseCategory expenseCategory =
                ExpenseCategory.builder()
                        .expenseCategoryId(1L)
                        .expenseCategoryName("??????")
                        .build();

        List<ExpenseCategory> expenseCategories = Arrays.asList(expenseCategory);

        DetailExpenseCategory detailExpenseCategory =
                DetailExpenseCategory.builder()
                        .detailExpenseCategoryId(1L)
                        .detailExpenseCategoryName("??????")
                        .expenseCategory(expenseCategory)
                        .build();

        List<DetailExpenseCategory> detailExpenseCategories = Arrays.asList(
                detailExpenseCategory
        );

        @Test
        @DisplayName("?????? ???????????? ?????? - ?????? : Mysql")
        void getExpenseCategories_success_mysql() {
            //given
            List<ExpenseCategory> redisExpenseCategories = new ArrayList<>();

            given(redisService.getList(any(), eq(ExpenseCategory.class)))
                    .willReturn(redisExpenseCategories);

            given(expenseCategoryRepository.findAll())
                    .willReturn(expenseCategories);

            given(detailExpenseCategoryRepository.findAllByExpenseCategory(expenseCategory))
                    .willReturn(detailExpenseCategories);

            //when
            List<ExpenseCategoryDto> expenseCategoryDtos = expenseServiceImpl.getExpenseCategories();

            //then
            assertEquals(expenseCategoryDtos.get(0).getExpenseCategoryId(), 1L);
            assertEquals(expenseCategoryDtos.get(0).getExpenseCategoryName(), "??????");
            assertEquals(expenseCategoryDtos.get(0)
                    .getDetailExpenseCategoryDtos().get(0).getDetailExpenseCategoryId(), 1L);
            assertEquals(expenseCategoryDtos.get(0)
                    .getDetailExpenseCategoryDtos().get(0).getDetailExpenseCategoryName(), "??????");
        }

        @Test
        @DisplayName("?????? ???????????? ?????? - ?????? : Redis")
        void getExpenseCategories_success_redis() {
            //given
            given(redisService.getList(any(), eq(ExpenseCategory.class)))
                    .willReturn(expenseCategories);

            given(redisService.getList(any(), eq(DetailExpenseCategory.class)))
                    .willReturn(detailExpenseCategories);

            //when
            List<ExpenseCategoryDto> expenseCategoryDtos = expenseServiceImpl.getExpenseCategories();

            //then
            assertEquals(expenseCategoryDtos.get(0).getExpenseCategoryId(), 1L);
            assertEquals(expenseCategoryDtos.get(0).getExpenseCategoryName(), "??????");
            assertEquals(expenseCategoryDtos.get(0)
                    .getDetailExpenseCategoryDtos().get(0).getDetailExpenseCategoryId(), 1L);
            assertEquals(expenseCategoryDtos.get(0)
                    .getDetailExpenseCategoryDtos().get(0).getDetailExpenseCategoryName(), "??????");
        }
    }

    @Nested
    @DisplayName("???????????? ??????")
    class updateExpenseMethod {
        Member member = Member.builder()
            .memberId(1L)
            .email("hgd@gmail.com")
            .password("1234")
            .build();

        ExpenseInput input = ExpenseInput.builder()
            .detailExpenseCategoryId(1L)
            .expenseDt(LocalDate.now())
            .expenseItem("?????????")
            .expenseCash(3000)
            .expenseCard(0)
            .expenseMemo("??????")
            .build();

        DetailExpenseCategory detailExpenseCategory =
            DetailExpenseCategory.builder()
                .detailExpenseCategoryId(1L)
                .detailExpenseCategoryName("??????")
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
        @DisplayName("???????????? ?????? - ??????")
        void updateExpense_success() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

            given(expenseRepository.findByExpenseId(any()))
                .willReturn(Optional.of(expense));

            given(detailExpenseCategoryRepository.findByDetailExpenseCategoryId(any()))
                .willReturn(Optional.of(detailExpenseCategory));

            //when
            expenseServiceImpl.updateExpense("hgd@gmail.com", Arrays.asList(input));

            //then
            verify(expenseRepository, times(1)).saveAll(any());
        }

        @Test
        @DisplayName("???????????? ?????? - ?????? : ?????? ??????")
        void updateExpense_fail_notFoundMember() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());
            //when
            ExpenseException exception = assertThrows(ExpenseException.class,
                () -> expenseServiceImpl.updateExpense("hgd@gmail.com", Arrays.asList(input)));
            //then
            assertEquals(exception.getErrorCode().getDescription(), "?????? ???????????????.");
        }

        @Test
        @DisplayName("???????????? ?????? - ?????? : ???????????? ??????")
        void updateExpense_fail_notFoundExpense() {
            //given
            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

            given(expenseRepository.findByExpenseId(any()))
                .willReturn(Optional.empty());

            //when
            ExpenseException exception = assertThrows(ExpenseException.class,
                () -> expenseServiceImpl.updateExpense("hgd@gmail.com", Arrays.asList(input)));
            //then
            assertEquals(exception.getErrorCode().getDescription(), "?????? ?????????????????????.");
        }

        @Test
        @DisplayName("???????????? ?????? - ?????? : ???????????? ??????")
        void updateExpense_fail_notFoundCategory() {
            //given
            given(memberRepository.findByEmail(anyString()))
                    .willReturn(Optional.of(member));

            given(expenseRepository.findByExpenseId(any()))
                    .willReturn(Optional.of(expense));

            given(detailExpenseCategoryRepository.findByDetailExpenseCategoryId(any()))
                    .willReturn(Optional.empty());

            //when
            ExpenseException exception = assertThrows(ExpenseException.class,
                    () -> expenseServiceImpl.updateExpense("hgd@gmail.com", Arrays.asList(input)));
            //then
            assertEquals(exception.getErrorCode().getDescription(), "?????? ???????????? ?????????.");
        }

        @Test
        @DisplayName("???????????? ?????? - ?????? : ????????? ???????????? ?????? ??????Id")
        void updateExpense_fail_notMatchMemberAndExpense() {
            //given
            Expense wrongExpense = Expense.builder()
                    .expenseId(1L)
                    .member(Member.builder().memberId(2L).build())
                    .detailExpenseCategory(detailExpenseCategory)
                    .expenseDt(input.getExpenseDt())
                    .expenseCash(input.getExpenseCash())
                    .expenseCard(input.getExpenseCard())
                    .expenseMemo(input.getExpenseMemo())
                    .build();

            given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(member));

            given(expenseRepository.findByExpenseId(any()))
                .willReturn(Optional.of(wrongExpense));

            //when
            ExpenseException exception = assertThrows(ExpenseException.class,
                () -> expenseServiceImpl.updateExpense("hgd@gmail.com", Arrays.asList(input)));
            //then
            assertEquals(exception.getErrorCode().getDescription(), "??????????????? ????????? ????????????. ????????? ???????????????.");
        }
    }

    @Nested
    @DisplayName("???????????? ??????")
    class deleteExpenseMethod {
        Member member = Member.builder()
                .memberId(1L)
                .email("hgd@gmail.com")
                .password("1234")
                .build();

        List<DeleteExpenseInput> list = Arrays.asList(
                new DeleteExpenseInput(1L)
        );

        Expense expense = Expense.builder()
                .member(member)
                .detailExpenseCategory(new DetailExpenseCategory())
                .expenseDt(LocalDate.now())
                .expenseItem("??????")
                .expenseCash(1000)
                .expenseCard(0)
                .expenseMemo("???")
                .build();

        @Test
        @DisplayName("???????????? ?????? - ??????")
        void updateExpense_success() {
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(member));

            given(expenseRepository.findByExpenseId(any()))
                    .willReturn(Optional.of(expense));

            //when
            expenseServiceImpl.deleteExpense("hgd@gmail.com", list);

            //then
            verify(expenseRepository, times(1)).deleteAllByIdInBatch(any());
        }

        @Test
        @DisplayName("???????????? ?????? - ?????? : ?????? ??????")
        void updateExpense_fail_notFoundUser() {
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.empty());

            //when
            ExpenseException exception = assertThrows(ExpenseException.class,
                    () -> expenseServiceImpl.deleteExpense("hgd@gmail.com", list));

            //then
            assertEquals("?????? ???????????????.", exception.getErrorCode().getDescription());
        }

        @Test
        @DisplayName("???????????? ?????? - ?????? : ?????? ?????? Id")
        void updateExpense_fail_notFoundExpenseId() {
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(member));

            given(expenseRepository.findByExpenseId(any()))
                    .willReturn(Optional.empty());

            //when
            ExpenseException exception = assertThrows(ExpenseException.class,
                    () -> expenseServiceImpl.deleteExpense("hgd@gmail.com", list));

            //then
            assertEquals("?????? ?????????????????????.", exception.getErrorCode().getDescription());
        }

        @Test
        @DisplayName("???????????? ?????? - ?????? : ????????? ???????????? ?????? ??????Id")
        void updateExpense_fail_notMatchMemberAndExpense() {
            //given
            Expense wrongExpense = Expense.builder()
                    .member(Member.builder().memberId(2L).build())
                    .detailExpenseCategory(new DetailExpenseCategory())
                    .expenseDt(LocalDate.now())
                    .expenseItem("??????")
                    .expenseCash(1000)
                    .expenseCard(0)
                    .expenseMemo("???")
                    .build();

            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(member));

            given(expenseRepository.findByExpenseId(any()))
                    .willReturn(Optional.of(wrongExpense));

            //when
            ExpenseException exception = assertThrows(ExpenseException.class,
                    () -> expenseServiceImpl.deleteExpense("hgd@gmail.com", list));

            //then
            assertEquals("??????????????? ????????? ????????????. ????????? ???????????????.", exception.getErrorCode().getDescription());
        }
    }

    @Nested
    @DisplayName("??? ?????? ????????? ??????")
    class getExpenseMonthReportMethod {
        Member member = Member.builder()
                .memberId(1L)
                .email("hgd@gmail.com")
                .password("1234")
                .build();

        @Test
        @DisplayName("??? ?????? ????????? ?????? - ??????")
        void getExpenseMonthReport_success() {
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(member));


            List<ReportDto> result = Arrays.asList(
                    new ReportDto("??????/??????", 8800000, 27.07),
                    new ReportDto("??????/??????", 23000000, 70.76),
                    new ReportDto("??????/??????", 200000, 0.62),
                    new ReportDto("??????", 41000, 0.13),
                    new ReportDto("??????/??????", 462000, 1.42)
            );

            given(expenseQueryRepository.getMonthlyExpenseReport(any(), any(), any()))
                    .willReturn(result);

            //when
            List<ReportDto> reportDtos = expenseServiceImpl.getMonthlyExpenseReport(
                    "hgd@gmail.com",
                    LocalDate.of(2022, 10, 1),
                    LocalDate.of(2022, 10, 31)
            );

            //then
            assertEquals(8800000, reportDtos.get(0).getCategorySum());
            assertEquals(23000000, reportDtos.get(1).getCategorySum());
            assertEquals(200000, reportDtos.get(2).getCategorySum());
            assertEquals(41000, reportDtos.get(3).getCategorySum());
            assertEquals(462000, reportDtos.get(4).getCategorySum());
        }

        @Test
        @DisplayName("??? ?????? ????????? ?????? - ?????? : ?????? ??????")
        void getExpenseMonthReport_fail_noMember() {
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.empty());

            //when
            ExpenseException exception = assertThrows(ExpenseException.class,
                    () -> expenseServiceImpl.getMonthlyExpenseReport(
                            "hgd@gmail.com",
                            LocalDate.of(2022, 10, 1),
                            LocalDate.of(2022, 10, 31)
                    )
            );

            //then
            assertEquals(exception.getErrorCode(), ExpenseErrorCode.NO_MEMBER);
        }
    }

    @Nested
    @DisplayName("??? ?????? ????????? ??????")
    class getYearlyExpenseReportMethod {
        Member member = Member.builder()
                .memberId(1L)
                .email("hgd@gmail.com")
                .password("1234")
                .build();

        @Test
        @DisplayName("??? ?????? ????????? ?????? - ??????")
        void getYearlyExpenseReport_success() {
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.of(member));

            List<YearlyReportDto> reportDtos = Arrays.asList(
                    YearlyReportDto.builder()
                                .month(10)
                                .category("??????/??????")
                                .categorySum(8800000)
                                .categoryRatio(27.07)
                                .build(),
                    YearlyReportDto.builder()
                                .month(10)
                                .category("??????/??????")
                                .categorySum(23000000)
                                .categoryRatio(70.76)
                                .build(),
                    YearlyReportDto.builder()
                                .month(10)
                                .category("??????/??????")
                                .categorySum(200000)
                                .categoryRatio(0.62)
                                .build(),
                    YearlyReportDto.builder()
                                .month(10)
                                .category("??????")
                                .categorySum(41000)
                                .categoryRatio(0.13)
                                .build(),
                    YearlyReportDto.builder()
                                .month(10)
                                .category("??????/??????")
                                .categorySum(462000)
                                .categoryRatio(1.42)
                                .build()
            );

            given(expenseQueryRepository.getYearlyExpenseReport(any(),any(),any()))
                    .willReturn(reportDtos);

            //when
            List<YearlyExpenseReportDto> yearlyReportDtos =
                    expenseServiceImpl.getYearlyExpenseReport(
                            "hgd@gmail.com",
                            LocalDate.of(2022, 1, 1),
                            LocalDate.of(2022, 12, 31));

            //then
            assertEquals(2022, yearlyReportDtos.get(9).getYear());
            assertEquals(10, yearlyReportDtos.get(9).getMonth());
            assertEquals(8800000+23000000+200000+41000+462000, yearlyReportDtos.get(9).getMonthlySum());
            assertEquals(8800000, yearlyReportDtos.get(9).getExpenseReport().get(0).getCategorySum());
            assertEquals(23000000, yearlyReportDtos.get(9).getExpenseReport().get(1).getCategorySum());
            assertEquals(200000, yearlyReportDtos.get(9).getExpenseReport().get(2).getCategorySum());
            assertEquals(41000, yearlyReportDtos.get(9).getExpenseReport().get(3).getCategorySum());
            assertEquals(462000, yearlyReportDtos.get(9).getExpenseReport().get(4).getCategorySum());
        }

        @Test
        @DisplayName("??? ?????? ????????? ?????? - ?????? : ?????? ??????")
        void getYearlyExpenseReport_fail_noMember() {
            //given
            given(memberRepository.findByEmail(any()))
                    .willReturn(Optional.empty());

            //when
            ExpenseException exception = assertThrows(ExpenseException.class,
                    () -> expenseServiceImpl.getYearlyExpenseReport(
                            "hgd@gmail.com",
                            LocalDate.of(2022, 10, 1),
                            LocalDate.of(2022, 10, 31)
                    )
            );

            //then
            assertEquals(exception.getErrorCode(), ExpenseErrorCode.NO_MEMBER);
        }
    }

    @Nested
    @DisplayName("?????? ?????? ?????? ????????? ?????????")
    class getMonthlyExpenseCalendarMethod {

        Member member = Member.builder()
            .memberId(1L)
            .password("1234")
            .email("test@naver.com")
            .build();

        List<CalendarExpenseDto> calendarExpenseDtoList = new ArrayList<>(Arrays.asList(
            CalendarExpenseDto.builder().expenseDt(LocalDate.of(2022, 10, 2))
                .item("??????1").amount(98765).build(),
            CalendarExpenseDto.builder().expenseDt(LocalDate.of(2022, 10, 16))
                .item("??????2").amount(45678).build()
        ));

        @Test
        @DisplayName("??????")
        void getMonthlyExpenseCalendar_success() {
            //given
            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.of(member));

            given(expenseQueryRepository.getMonthlyExpenseCalendar(any(), any(), any()))
                .willReturn(calendarExpenseDtoList);

            //when
            List<CalendarExpenseDto> getMonthlyExpenseCalendar
                = expenseServiceImpl.getMonthlyExpenseCalendar("test@naver.com",
                LocalDate.of(2022, 10, 1),
                LocalDate.of(2022, 10, 31));

            //then
            verify(expenseQueryRepository, times(1))
                .getMonthlyExpenseCalendar(any(), any(), any());
            assertEquals(getMonthlyExpenseCalendar.get(0).getItem(),
                calendarExpenseDtoList.get(0).getItem());
        }

        @Test
        @DisplayName("?????? - ?????? ??????")
        void getMonthlyExpenseCalendar_no_member() {
            //given
            given(memberRepository.findByEmail(any()))
                .willReturn(Optional.empty());

            //when
            ExpenseException exception = assertThrows(ExpenseException.class,
                () -> expenseServiceImpl.getMonthlyExpenseCalendar("test@naver.com",
                    LocalDate.of(2022, 10, 1),
                    LocalDate.of(2022, 10, 31))
            );

            //then
            assertEquals(exception.getErrorCode(), ExpenseErrorCode.NO_MEMBER);
        }

    }

}