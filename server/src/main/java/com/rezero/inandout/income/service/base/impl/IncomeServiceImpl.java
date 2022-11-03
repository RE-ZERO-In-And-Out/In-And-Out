package com.rezero.inandout.income.service.base.impl;

import static com.rezero.inandout.exception.errorcode.IncomeErrorCode.NOT_MATCH_MEMBER_AND_INCOME;
import static com.rezero.inandout.exception.errorcode.IncomeErrorCode.NO_CATEGORY;
import static com.rezero.inandout.exception.errorcode.IncomeErrorCode.NO_INCOME;
import static com.rezero.inandout.exception.errorcode.IncomeErrorCode.NO_MEMBER;

import com.rezero.inandout.calendar.model.CalendarIncomeDto;
import com.rezero.inandout.exception.IncomeException;
import com.rezero.inandout.expense.entity.DetailExpenseCategory;
import com.rezero.inandout.expense.entity.ExpenseCategory;
import com.rezero.inandout.expense.model.DetailExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseCategoryDto;
import com.rezero.inandout.income.entity.DetailIncomeCategory;
import com.rezero.inandout.income.entity.Income;
import com.rezero.inandout.income.entity.IncomeCategory;
import com.rezero.inandout.income.model.DeleteIncomeInput;
import com.rezero.inandout.income.model.DetailIncomeCategoryDto;
import com.rezero.inandout.income.model.IncomeCategoryDto;
import com.rezero.inandout.income.model.IncomeDto;
import com.rezero.inandout.income.model.IncomeInput;
import com.rezero.inandout.income.repository.DetailIncomeCategoryRepository;
import com.rezero.inandout.income.repository.IncomeCategoryRepository;
import com.rezero.inandout.income.repository.IncomeQueryRepository;
import com.rezero.inandout.income.repository.IncomeRepository;
import com.rezero.inandout.income.service.base.IncomeService;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.redis.RedisService;
import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.model.YearlyIncomeReportDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {


    private final MemberRepository memberRepository;
    private final IncomeRepository incomeRepository;
    private final IncomeCategoryRepository incomeCategoryRepository;
    private final DetailIncomeCategoryRepository detailIncomeCategoryRepository;
    private final IncomeQueryRepository incomeQueryRepository;
    private final RedisService redisService;

    private static final String INCOME_CATEGORY_REDIS_KEY = "수입카테고리";


    @Override
    public void addIncome(String email, List<IncomeInput> incomeInputList) {
        Member member = findMemberByEmail(email);

        List<Income> incomes = new ArrayList<>();

        for (IncomeInput item : incomeInputList) {
            DetailIncomeCategory detailIncomeCategory = findDetailIncomeCategoryById(
                item.getDetailIncomeCategoryId());

            incomes.add(
                Income.builder()
                    .member(member)
                    .detailIncomeCategory(detailIncomeCategory)
                    .incomeDt(item.getIncomeDt())
                    .incomeItem(item.getIncomeItem())
                    .incomeAmount(item.getIncomeAmount())
                    .incomeMemo(item.getIncomeMemo())
                    .build()
            );
        }

        incomeRepository.saveAll(incomes);
    }

    @Override
    public List<IncomeDto> getIncomeList(String email, LocalDate startDt, LocalDate endDt) {
        Member member = findMemberByEmail(email);

        List<Income> incomeList = incomeRepository.findAllByMemberAndIncomeDtBetweenOrderByIncomeDt(
            member, startDt, endDt);

        return Income.toDtoList(incomeList);
    }

    @Override
    public List<IncomeCategoryDto> getIncomeCategoryList() {
        List<IncomeCategoryDto> incomeCategoryDtoList = new ArrayList<>();

        List<IncomeCategory> incomeCategoryList =
                redisService.getList(INCOME_CATEGORY_REDIS_KEY, IncomeCategory.class);

        if (incomeCategoryList.size() > 0) {
            for (IncomeCategory incomeCategory : incomeCategoryList) {
                IncomeCategoryDto incomeCategoryDto =
                        IncomeCategory.toDto(incomeCategory);

                List<DetailIncomeCategory> detailIncomeCategories =
                        redisService.getList(
                                incomeCategory.getIncomeCategoryName(),
                                DetailIncomeCategory.class);

                incomeCategoryDto.setDetailIncomeCategoryDtoList(
                        DetailIncomeCategory.toDtoList(detailIncomeCategories));

                incomeCategoryDtoList.add(incomeCategoryDto);
            }
        } else {
            incomeCategoryList = incomeCategoryRepository.findAll();

            redisService.putList(INCOME_CATEGORY_REDIS_KEY, incomeCategoryList);

            for (IncomeCategory incomeCategory : incomeCategoryList) {
                IncomeCategoryDto incomeCategoryDto = IncomeCategory.toDto(incomeCategory);

                List<DetailIncomeCategory> detailIncomeCategoryList
                        = detailIncomeCategoryRepository.findAllByIncomeCategory(incomeCategory);

                redisService.putList(incomeCategory.getIncomeCategoryName(), detailIncomeCategoryList);

                incomeCategoryDto.setDetailIncomeCategoryDtoList(
                        DetailIncomeCategory.toDtoList(detailIncomeCategoryList));

                incomeCategoryDtoList.add(incomeCategoryDto);
            }
        }

        return incomeCategoryDtoList;
    }

    @Override
    public List<DetailIncomeCategoryDto> getDetailIncomeCategory() {
        List<DetailIncomeCategory> detailIncomeCategoryList
            = detailIncomeCategoryRepository.findAll();

        return DetailIncomeCategory.toDtoList(detailIncomeCategoryList);
    }

    @Override
    public void updateIncome(String email, List<IncomeInput> incomeInputList) {
        Member member = findMemberByEmail(email);

        List<Income> incomeList = new ArrayList<>();

        for (IncomeInput item : incomeInputList) {
            DetailIncomeCategory detailIncomeCategory = findDetailIncomeCategoryById(
                item.getDetailIncomeCategoryId());

            Income income = findByIncomeId(item.getIncomeId());
            validateMatchingMemberAndIncome(member, income.getIncomeId());

            income.setDetailIncomeCategory(detailIncomeCategory);
            income.setIncomeDt(item.getIncomeDt());
            income.setIncomeItem(item.getIncomeItem());
            income.setIncomeAmount(item.getIncomeAmount());
            income.setIncomeMemo(item.getIncomeMemo());

            incomeList.add(income);
        }

        incomeRepository.saveAll(incomeList);

    }

    @Override
    public void deleteIncome(String email, List<DeleteIncomeInput> deleteIncomeInputList) {
        Member member = findMemberByEmail(email);

        List<Long> deleteIncomeIdList = new ArrayList<>();
        for (DeleteIncomeInput item : deleteIncomeInputList) {
            findByIncomeId(item.getIncomeId());
            validateMatchingMemberAndIncome(member, item.getIncomeId());
            deleteIncomeIdList.add(item.getIncomeId());
        }

        incomeRepository.deleteAllByIdInBatch(deleteIncomeIdList);
    }

    @Override
    public List<ReportDto> getMonthlyIncomeReport(String email, LocalDate startDt, LocalDate endDt) {

        Member member = findMemberByEmail(email);

        List<ReportDto> reportDtoList
            = incomeQueryRepository.getMonthlyIncomeReport(member.getMemberId(), startDt, endDt);

        if(reportDtoList.size() > 0) {
            int monthlyIncomeSum
                = incomeQueryRepository.getMonthlyIncomeSum(member.getMemberId(), startDt, endDt);

            for (ReportDto item : reportDtoList) {
                item.setCategoryRatio(
                    Math.round(item.getCategoryRatio() / monthlyIncomeSum * 100) / 100.0
                );
            }
        }

        return reportDtoList;
    }

    @Override
    public List<YearlyIncomeReportDto> getYearlyIncomeReport(String email, LocalDate startDt,
        LocalDate endDt) {

        Member member = findMemberByEmail(email);

        LocalDate countDate = startDt;

        List<YearlyIncomeReportDto> yearlyReportDtoList = new ArrayList<>();

        while(countDate.isBefore(endDt)) {
            List<ReportDto> reportDtoList
                = getMonthlyIncomeReport(member.getEmail(),
                countDate, countDate.plusMonths(1).minusDays(1));

            int thisSum = 0;

            for (ReportDto item : reportDtoList) {
                thisSum += item.getCategorySum();
            }

            yearlyReportDtoList.add(
                YearlyIncomeReportDto.builder()
                    .year(countDate.getYear())
                    .month(countDate.getMonthValue())
                    .monthlySum(thisSum)
                    .incomeReport(reportDtoList)
                    .build()
            );

            countDate = countDate.plusMonths(1);
        }

        return yearlyReportDtoList;
    }

    @Override
    public List<CalendarIncomeDto> getMonthlyIncomeCalendar(String email, LocalDate startDt, LocalDate endDt) {
        Member member = findMemberByEmail(email);

        return incomeQueryRepository.getMonthlyIncomeCalendar(member.getMemberId(), startDt, endDt);
    }


    private DetailIncomeCategory findDetailIncomeCategoryById(Long detailIncomeCategoryId) {
        return detailIncomeCategoryRepository
            .findByDetailIncomeCategoryId(detailIncomeCategoryId)
            .orElseThrow(() -> new IncomeException(NO_CATEGORY));
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new IncomeException(NO_MEMBER));
    }

    private Income findByIncomeId(Long incomeId) {
        return incomeRepository.findById(incomeId)
            .orElseThrow(() -> new IncomeException(NO_INCOME));
    }

    private void validateMatchingMemberAndIncome(Member loginMember, Long incomeId) {
        Income income = incomeRepository.findById(incomeId)
            .orElseThrow(() -> new IncomeException(NO_INCOME));
        Long memberId = income.getMember().getMemberId();
        if(!Objects.equals(loginMember.getMemberId(), memberId)) {
            throw new IncomeException(NOT_MATCH_MEMBER_AND_INCOME);
        }
    }

}
