package com.rezero.inandout.excel.service.impl;

import com.rezero.inandout.excel.component.ExcelDownloadComponent;
import com.rezero.inandout.excel.model.ExpenseExcelDto;
import com.rezero.inandout.excel.model.IncomeExcelDto;
import com.rezero.inandout.excel.model.YearlyExcelDto;
import com.rezero.inandout.excel.service.ExcelService;
import com.rezero.inandout.exception.MemberException;
import com.rezero.inandout.exception.errorcode.MemberErrorCode;
import com.rezero.inandout.expense.model.DetailExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseCategoryDto;
import com.rezero.inandout.expense.model.ExpenseDto;
import com.rezero.inandout.expense.service.base.impl.ExpenseServiceImpl;
import com.rezero.inandout.income.model.DetailIncomeCategoryDto;
import com.rezero.inandout.income.model.IncomeCategoryDto;
import com.rezero.inandout.income.model.IncomeDto;
import com.rezero.inandout.income.service.base.impl.IncomeServiceImpl;
import com.rezero.inandout.member.entity.Member;
import com.rezero.inandout.member.repository.MemberRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    @AllArgsConstructor
    public static class ColConfig {
        public String colName;
        public int colWidth;
    }

    private final ExcelDownloadComponent excelDownloadComponent;
    private final MemberRepository memberRepository;
    private final IncomeServiceImpl incomeService;
    private final ExpenseServiceImpl expenseService;

    @Override
    public ResponseEntity<InputStreamResource> downloadIncomeExcelFile(
        HttpServletRequest request, String email, LocalDate startDt, LocalDate endDt)
        throws IOException {

        Member member = findMemberByEmail(email);

        List<IncomeExcelDto> excelRowData = setIncomeExcelDataList(email, startDt, endDt);

        final ArrayList<ColConfig> headColumn = new ArrayList<>(Arrays.asList(
            new ColConfig("No", 1000),
            new ColConfig("일자", 3000),
            new ColConfig("카테고리", 3000),
            new ColConfig("세부카테고리", 4000),
            new ColConfig("내역", 8000),
            new ColConfig("금액", 3000),
            new ColConfig("메모", 5000))
        );

        try {
            return excelDownloadComponent.downloadIncomeExcelFile(request, email, startDt, endDt,
                excelRowData, headColumn);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadExpenseExcelFile(HttpServletRequest request,
        String email, LocalDate startDt, LocalDate endDt) throws IOException {

        Member member = findMemberByEmail(email);

        List<ExpenseExcelDto> expenseExcelDtoList = setExpenseExcelDataList(email, startDt, endDt);

        final ArrayList<ColConfig> headColumn = new ArrayList<>(Arrays.asList(
            new ColConfig("No", 1000),
            new ColConfig("일자", 3000),
            new ColConfig("카테고리", 3000),
            new ColConfig("세부카테고리", 4000),
            new ColConfig("내역", 8000),
            new ColConfig("카드금액", 3000),
            new ColConfig("현금액", 3000),
            new ColConfig("메모", 5000))
        );

        try {
            return excelDownloadComponent.downloadExpenseExcelFile(
                request, email, startDt, endDt, expenseExcelDtoList, headColumn);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadYearlyReportExcelFile(
        HttpServletRequest request, String email, LocalDate startDt,
        List<YearlyExcelDto> yearlyExcelDtoList) throws IOException {

        Member member = findMemberByEmail(email);

        ArrayList<String> period = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            LocalDate cur = startDt.plusMonths(i);
            period.add(String.format("%4d-%02d", cur.getYear(), cur.getMonthValue()));
        }

        final ArrayList<ColConfig> headColumn = new ArrayList<>(Arrays.asList(
            new ColConfig("항목", 5000),
            new ColConfig(period.get(0), 3000),
            new ColConfig(period.get(1), 3000),
            new ColConfig(period.get(2), 3000),
            new ColConfig(period.get(3), 3000),
            new ColConfig(period.get(4), 3000),
            new ColConfig(period.get(5), 3000),
            new ColConfig(period.get(6), 3000),
            new ColConfig(period.get(7), 3000),
            new ColConfig(period.get(8), 3000),
            new ColConfig(period.get(9), 3000),
            new ColConfig(period.get(10), 3000),
            new ColConfig(period.get(11), 3000))
        );

        try {
            return excelDownloadComponent.downloadYearlyReport(
                request, email, startDt, startDt.plusMonths(12).minusDays(1),
                yearlyExcelDtoList, headColumn);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private List<IncomeExcelDto> setIncomeExcelDataList(String email, LocalDate startDt, LocalDate endDt) {

        HashMap<Long, String> detailIncomeCategoryIdToNameMap = new HashMap<>();
        for (DetailIncomeCategoryDto item : incomeService.getDetailIncomeCategory()) {
            detailIncomeCategoryIdToNameMap.put(item.getDetailIncomeCategoryId(),
                item.getDetailIncomeCategoryName());
        }

        HashMap<String, String> detailIncomeCategoryNameToIncomeCategoryNameMap
            = new HashMap<>();

        for (IncomeCategoryDto item : incomeService.getIncomeCategoryList()) {
            for (DetailIncomeCategoryDto detailItem : item.getDetailIncomeCategoryDtoList()) {
                detailIncomeCategoryNameToIncomeCategoryNameMap.put(
                    detailItem.getDetailIncomeCategoryName(), item.getIncomeCategoryName());
            }
        }

        List<IncomeDto> getIncomeList = incomeService.getIncomeList(email, startDt, endDt);

        List<IncomeExcelDto> incomeExcelDtoList = new ArrayList<>();

        for (IncomeDto item : getIncomeList) {
            IncomeExcelDto addDto = IncomeDto.toExcelDto(item);
            addDto.setIncomeCategoryName(detailIncomeCategoryNameToIncomeCategoryNameMap.get(
                (detailIncomeCategoryIdToNameMap.get(item.getDetailIncomeCategoryId()))));
            addDto.setDetailIncomeCategoryName(detailIncomeCategoryIdToNameMap.get(
                item.getDetailIncomeCategoryId()));
            incomeExcelDtoList.add(addDto);
        }

        return incomeExcelDtoList;
    }

    private List<ExpenseExcelDto> setExpenseExcelDataList(String email, LocalDate startDt, LocalDate endDt) {

        HashMap<Long, String> detailExpenseCategoryIdToNameMap = new HashMap<>();
        for (DetailExpenseCategoryDto item : expenseService.getDetailExpenseCategory()) {
            detailExpenseCategoryIdToNameMap.put(item.getDetailExpenseCategoryId(),
                item.getDetailExpenseCategoryName());
        }

        HashMap<String, String> detailExpenseCategoryNameToExpenseCategoryNameMap
            = new HashMap<>();

        for (ExpenseCategoryDto item : expenseService.getExpenseCategories()) {
            for (DetailExpenseCategoryDto detailItem : item.getDetailExpenseCategoryDtos()) {
                detailExpenseCategoryNameToExpenseCategoryNameMap.put(
                    detailItem.getDetailExpenseCategoryName(), item.getExpenseCategoryName());
            }
        }

        List<ExpenseDto> getExpenseList = expenseService.getExpenses(email, startDt, endDt);

        List<ExpenseExcelDto> expenseExcelDtoList = new ArrayList<>();

        for (ExpenseDto item : getExpenseList) {
            ExpenseExcelDto addDto = ExpenseDto.toExcelDto(item);
            addDto.setExpenseCategoryName(detailExpenseCategoryNameToExpenseCategoryNameMap.get(
                (detailExpenseCategoryIdToNameMap.get(item.getDetailExpenseCategoryId()))));
            addDto.setDetailExpenseCategoryName(detailExpenseCategoryIdToNameMap.get(
                item.getDetailExpenseCategoryId()));
            expenseExcelDtoList.add(addDto);
        }

        return expenseExcelDtoList;
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_EXIST));
    }
}
