package com.rezero.inandout.report.service.impl;


import com.rezero.inandout.exception.MemberException;
import com.rezero.inandout.exception.errorcode.MemberErrorCode;
import com.rezero.inandout.income.repository.IncomeQueryRepository;
import java.time.LocalDate;
import java.util.List;

import com.rezero.inandout.exception.ExpenseException;
import com.rezero.inandout.exception.errorcode.ExpenseErrorCode;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.repository.ExpenseQueryRepository;
import com.rezero.inandout.report.service.ReportService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final MemberRepository memberRepository;
    private final IncomeQueryRepository incomeQueryRepository;
    private final ExpenseQueryRepository expenseQueryRepository;
    
    @Override
    public List<ReportDto> getMonthlyIncomeReport(String email, LocalDate startDt, LocalDate endDt) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_EXIST_MEMBER));

        List<ReportDto> monthlyIncomeReport
            =  incomeQueryRepository.getMonthlyIncomeReport(member.getMemberId(), startDt, endDt);

        return monthlyIncomeReport;
    }

    @Override
    public List<ReportDto> getExpenseMonthReport(String email, LocalDate startDt, LocalDate endDt) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ExpenseException(ExpenseErrorCode.NO_MEMBER));

        return expenseQueryRepository.getExpenseMonthReport(member, startDt, endDt);
    }
    
}