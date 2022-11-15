package com.rezero.inandout.expense.service.base.impl;

import com.rezero.inandout.calendar.model.CalendarExpenseDto;
import com.rezero.inandout.exception.ExpenseException;
import com.rezero.inandout.exception.errorcode.ExpenseErrorCode;
import com.rezero.inandout.expense.entity.DetailExpenseCategory;
import com.rezero.inandout.expense.entity.Expense;
import com.rezero.inandout.expense.entity.ExpenseCategory;
import com.rezero.inandout.expense.model.DeleteExpenseInput;
import com.rezero.inandout.expense.model.DetailExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseDto;
import com.rezero.inandout.expense.model.ExpenseInput;
import com.rezero.inandout.expense.repository.DetailExpenseCategoryRepository;
import com.rezero.inandout.expense.repository.ExpenseCategoryRepository;
import com.rezero.inandout.expense.repository.ExpenseQueryRepository;
import com.rezero.inandout.expense.repository.ExpenseRepository;
import com.rezero.inandout.expense.service.base.ExpenseService;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.redis.RedisService;
import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.model.YearlyExpenseReportDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rezero.inandout.report.model.YearlyReportDto;
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
    private final ExpenseQueryRepository expenseQueryRepository;
    private final RedisService redisService;

    private static final String EXPENSE_CATEGORY_REDIS_KEY = "지출카테고리";

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
    @Transactional
    public List<ExpenseCategoryDto> getExpenseCategories() {
        List<ExpenseCategoryDto> expenseCategoryDtos = new ArrayList<>();

        List<ExpenseCategory> expenseCategories =
                redisService.getList(EXPENSE_CATEGORY_REDIS_KEY, ExpenseCategory.class);

        if (expenseCategories.size() > 0) {
            for (ExpenseCategory expenseCategory : expenseCategories) {
                ExpenseCategoryDto expenseCategoryDto =
                        ExpenseCategoryDto.toDto(expenseCategory);

                List<DetailExpenseCategory> detailExpenseCategories =
                        redisService.getList(
                                EXPENSE_CATEGORY_REDIS_KEY
                                        + expenseCategory.getExpenseCategoryId(),
                                DetailExpenseCategory.class);

                expenseCategoryDto.setDetailExpenseCategoryDtos(
                        DetailExpenseCategoryDto.toDtos(detailExpenseCategories));

                expenseCategoryDtos.add(expenseCategoryDto);
            }
        } else {
            expenseCategories = expenseCategoryRepository.findAll();

            redisService.putList(EXPENSE_CATEGORY_REDIS_KEY, expenseCategories);

            for (ExpenseCategory expenseCategory : expenseCategories) {
                ExpenseCategoryDto expenseCategoryDto = ExpenseCategoryDto.toDto(expenseCategory);

                List<DetailExpenseCategory> detailExpenseCategories =
                        detailExpenseCategoryRepository
                                .findAllByExpenseCategory(expenseCategory);

                redisService.putList(
                        EXPENSE_CATEGORY_REDIS_KEY
                                + expenseCategory.getExpenseCategoryId(),
                        detailExpenseCategories);

                expenseCategoryDto.setDetailExpenseCategoryDtos(
                        DetailExpenseCategoryDto.toDtos(detailExpenseCategories));

                expenseCategoryDtos.add(expenseCategoryDto);
            }
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

    @Override
    public List<ReportDto> getMonthlyExpenseReport(String email, LocalDate startDt, LocalDate endDt) {

        Member member = findMemberByEmail(email);

        List<ReportDto> reportDtos
                = expenseQueryRepository.getMonthlyExpenseReport(member, startDt, endDt);

        int monthlyExpenseSum = 0;

        for (ReportDto reportDto : reportDtos) {
            monthlyExpenseSum += reportDto.getCategorySum();
        }

        for (ReportDto reportDto : reportDtos) {
            reportDto.setCategoryRatio(
                    Math.round(reportDto.getCategoryRatio() / monthlyExpenseSum * 100) / 100.0
            );
        }

        return reportDtos;
    }

    @Override
    public List<YearlyExpenseReportDto> getYearlyExpenseReport(
            String email, LocalDate startDt, LocalDate endDt) {

        Member member = findMemberByEmail(email);

        List<YearlyExpenseReportDto> yearlyExpenseReportDtos = new ArrayList<>();

        List<YearlyReportDto> yearlyReportDtos =
                expenseQueryRepository.getYearlyExpenseReport(member, startDt, endDt);

        HashMap<Integer, Integer> monthlySum = new HashMap<>();

        for (YearlyReportDto dto : yearlyReportDtos) {
            monthlySum.put(dto.getMonth(),
                    monthlySum.getOrDefault(dto.getMonth(), 0) + dto.getCategorySum()
            );
        }

        int curYear = startDt.getYear();
        int curMonth = startDt.getMonthValue();

        for (int i = 0; i < 12; i++) {
            List<ReportDto> reportDtos = new ArrayList<>();
            for (YearlyReportDto dto : yearlyReportDtos) {
                if (dto.getMonth() == curMonth) {
                    reportDtos.add(
                            ReportDto.builder()
                                    .category(dto.getCategory())
                                    .categorySum(dto.getCategorySum())
                                    .categoryRatio(
                                            Math.round(
                                                    (double)dto.getCategorySum()
                                                    / (double)monthlySum.get(curMonth)
                                                    * 100 * 100) / 100.00
                                    ).build()
                    );
                }
            }

            int curSum = 0;
            if (monthlySum.containsKey(curMonth)) {
                curSum = monthlySum.get(curMonth);
            }

            yearlyExpenseReportDtos.add(
                    YearlyExpenseReportDto.builder()
                            .year(curYear)
                            .month(curMonth)
                            .monthlySum(curSum)
                            .expenseReport(reportDtos)
                            .build()
            );

            curMonth++;
            if (curMonth > 12) {
                curMonth = 1;
                curYear++;
            }
        }

        return yearlyExpenseReportDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarExpenseDto> getMonthlyExpenseCalendar(String email, LocalDate startDt, LocalDate endDt) {
        Member member = findMemberByEmail(email);

        return expenseQueryRepository.getMonthlyExpenseCalendar(member.getMemberId(), startDt, endDt);
    }

    @Override
    public List<DetailExpenseCategoryDto> getDetailExpenseCategory() {
        List<DetailExpenseCategory> detailExpenseCategoryList
            = detailExpenseCategoryRepository.findAll();

        return DetailExpenseCategoryDto.toDtos(detailExpenseCategoryList);
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
