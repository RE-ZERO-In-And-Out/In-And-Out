package com.rezero.inandout.excel.controller;

import com.rezero.inandout.excel.service.impl.ExcelServiceImpl;
import io.swagger.annotations.ApiParam;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExcelController {

    private final ExcelServiceImpl excelService;

    @GetMapping("/excel/income")
    public ResponseEntity<InputStreamResource> downloadIncomeExcelFile(HttpServletRequest request,
                    Principal principal,
                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    @ApiParam(value = "조회할 기간의 시작일", example = "2022-10-01") LocalDate startDt,
                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    @ApiParam(value = "조회할 기간의 마지막일", example = "2022-10-31") LocalDate endDt)
        throws IOException {

            return excelService.downloadIncomeExcelFile(request, principal.getName(), startDt, endDt);
    }

    @GetMapping("/excel/expense")
    public ResponseEntity<InputStreamResource> downloadExpenseExcelFile(HttpServletRequest request,
        Principal principal,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @ApiParam(value = "조회할 기간의 시작일", example = "2022-10-01") LocalDate startDt,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @ApiParam(value = "조회할 기간의 마지막일", example = "2022-10-31") LocalDate endDt)
        throws IOException {

        return excelService.downloadExpenseExcelFile(request, principal.getName(), startDt, endDt);
    }


}
