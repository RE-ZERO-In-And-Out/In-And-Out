package com.rezero.inandout.excel.service;

import com.rezero.inandout.excel.model.YearlyExcelDto;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

public interface ExcelService {

    ResponseEntity<InputStreamResource> downloadIncomeExcelFile(HttpServletRequest request,
        String email, LocalDate startDt, LocalDate endDt)
        throws IOException;

    ResponseEntity<InputStreamResource> downloadExpenseExcelFile(
        HttpServletRequest request, String email, LocalDate startDt, LocalDate endDt)
        throws IOException;

    ResponseEntity<InputStreamResource> downloadYearlyReportExcelFile(
        HttpServletRequest request, String email, LocalDate startDt,
        List<YearlyExcelDto> yearlyExcelDtoList) throws IOException;
}
