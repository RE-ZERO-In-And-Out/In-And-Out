package com.rezero.inandout.income.service;

import com.rezero.inandout.income.entity.DetailIncomeCategory;
import com.rezero.inandout.income.entity.Income;
import com.rezero.inandout.income.model.IncomeInput;
import com.rezero.inandout.income.repository.DetailIncomeCategoryRepository;
import com.rezero.inandout.income.repository.IncomeRepository;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {


    private final MemberRepository memberRepository;
    private final IncomeRepository incomeRepository;
    private final DetailIncomeCategoryRepository detailIncomeCategoryRepository;


    @Override
    public void addIncome(String email, List<IncomeInput> incomeInputList) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("없는 맴버입니다."));

        List<Income> incomes = new ArrayList<>();

        for (IncomeInput input : incomeInputList) {
            DetailIncomeCategory detailIncomeCategory = findDetailIncomeCategoryById(
                input.getDetailIncomeCategoryId());

            incomes.add(
                Income.builder()
                    .member(member)
                    .detailIncomeCategory(detailIncomeCategory)
                    .incomeDt(input.getIncomeDt())
                    .incomeItem(input.getIncomeItem())
                    .incomeAmount(input.getIncomeAmount())
                    .incomeMemo(input.getIncomeMemo())
                    .build()
            );
        }

        incomeRepository.saveAll(incomes);
    }

    private DetailIncomeCategory findDetailIncomeCategoryById(Long detailIncomeCategoryId) {
        return detailIncomeCategoryRepository
            .findByDetailIncomeCategoryId(detailIncomeCategoryId)
            .orElseThrow(() -> new RuntimeException("없는 카테고리 입니다."));
    }
}
