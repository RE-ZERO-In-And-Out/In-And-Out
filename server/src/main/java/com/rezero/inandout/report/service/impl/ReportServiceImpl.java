package com.rezero.inandout.report.service.impl;

import com.rezero.inandout.exception.MemberException;
import com.rezero.inandout.exception.errorcode.MemberErrorCode;
import com.rezero.inandout.income.repository.IncomeQueryRepository;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.service.ReportService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final MemberRepository memberRepository;
    private final IncomeQueryRepository incomeQueryRepository;
    @Override
    public List<ReportDto> getMonthlyIncomeReport(String email, LocalDate startDt, LocalDate endDt) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberException(MemberErrorCode.NOT_EXIST_MEMBER));

        List<ReportDto> monthlyIncomeReport
            =  incomeQueryRepository.getMonthlyIncomeReport(member.getMemberId(), startDt, endDt);

        for (ReportDto item : monthlyIncomeReport) {
            double categoryRatio = item.getCategoryRatio();
            item.setCategoryRatio(Math.round(categoryRatio * 100) / 100.0);
        }

        return monthlyIncomeReport;
    }
}