package com.rezero.inandout.income.service;

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
import com.rezero.inandout.income.repository.IncomeRepository;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
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

        List<Income> incomeList = incomeRepository.findAllByMemberAndIncomeDtBetween(
            member, startDt, endDt);

        return Income.toDtoList(incomeList);
    }

    @Override
    public List<IncomeCategoryDto> getIncomeCategoryList() {
        List<IncomeCategory> incomeCategoryList = incomeCategoryRepository.findAll();

        List<IncomeCategoryDto> incomeCategoryDtoList = new ArrayList<>();

        for (IncomeCategory item : incomeCategoryList) {
            IncomeCategoryDto incomeCategoryDto = IncomeCategory.toDto(item);

            List<DetailIncomeCategory> detailIncomeCategoryList
                = detailIncomeCategoryRepository.findAllByIncomeCategory(item);

            incomeCategoryDto.setDetailIncomeCategoryDtoList(
                DetailIncomeCategory.toDtoList(detailIncomeCategoryList));

            incomeCategoryDtoList.add(incomeCategoryDto);
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


    private DetailIncomeCategory findDetailIncomeCategoryById(Long detailIncomeCategoryId) {
        return detailIncomeCategoryRepository
            .findByDetailIncomeCategoryId(detailIncomeCategoryId)
            .orElseThrow(() -> new RuntimeException("없는 카테고리 입니다."));
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("없는 맴버입니다."));
    }

    private Income findByIncomeId(Long incomeId) {
        return incomeRepository.findById(incomeId)
            .orElseThrow(() -> new RuntimeException("없는 수입내역 입니다."));
    }

    private void validateMatchingMemberAndIncome(Member loginMember, Long incomeId) {
        Income income = incomeRepository.findById(incomeId)
            .orElseThrow(() -> new RuntimeException("없는 수입내역 입니다."));
        Long memberId = income.getMember().getMemberId();
        if(!Objects.equals(loginMember.getMemberId(), memberId)) {
            throw new RuntimeException("수입내역의 주인이 아닙니다. 잘못된 요청입니다.");
        }
    }

}
