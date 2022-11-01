package com.rezero.inandout.excel.service;

import java.io.IOException;
import java.time.LocalDate;
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
}
