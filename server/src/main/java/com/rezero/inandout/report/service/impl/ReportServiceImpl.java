package com.rezero.inandout.report.service.impl;

import com.rezero.inandout.exception.ExpenseException;
import com.rezero.inandout.exception.errorcode.ExpenseErrorCode;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.repository.ExpenseQueryRepository;
import com.rezero.inandout.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final MemberRepository memberRepository;
    private final ExpenseQueryRepository expenseQueryRepository;

    @Override
    public List<ReportDto> getExpenseMonthReport(String email, LocalDate startDt, LocalDate endDt) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ExpenseException(ExpenseErrorCode.NO_MEMBER));

        return expenseQueryRepository.getExpenseMonthReport(member, startDt, endDt);
    }
}