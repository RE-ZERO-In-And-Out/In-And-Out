package com.rezero.inandout.excel.controller;

import com.rezero.inandout.excel.model.YearlyExcelDto;
import com.rezero.inandout.excel.service.impl.ExcelServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/excel")
public class ExcelController {

    private final ExcelServiceImpl excelService;

    @ApiOperation(value = "수입내역 Excel 출력 API",
        notes = "조회할 기간을 입력하면 해당하는 수입내역이 엑셀로 출력됩니다.")
    @GetMapping("/income")
    public ResponseEntity<InputStreamResource> downloadIncomeExcelFile(
                    HttpServletRequest request, Principal principal,
                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    @ApiParam(value = "조회할 기간의 시작일", example = "2022-10-01") LocalDate startDt,
                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    @ApiParam(value = "조회할 기간의 마지막일", example = "2022-10-31") LocalDate endDt)
        throws IOException {

        return excelService.downloadIncomeExcelFile(request, principal.getName(), startDt, endDt);
    }

    @ApiOperation(value = "지출내역 Excel 출력 API",
        notes = "조회할 기간을 입력하면 해당하는 지출내역이 엑셀로 출력됩니다.")
    @GetMapping("/expense")
    public ResponseEntity<InputStreamResource> downloadExpenseExcelFile(
                    HttpServletRequest request, Principal principal,
                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    @ApiParam(value = "조회할 기간의 시작일", example = "2022-10-01") LocalDate startDt,
                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    @ApiParam(value = "조회할 기간의 마지막일", example = "2022-10-31") LocalDate endDt)
            throws IOException {

        return excelService.downloadExpenseExcelFile(request, principal.getName(), startDt, endDt);
    }

    @ApiOperation(value = "연간보고서 Excel 출력 API",
        notes = "조회할 시작일 입력한 후 연간보고서 페이지의 데이터를 Json 형태로 Post 하면 "
            + "시작일 기준 12개월의 연간보고서가 엑셀로 출력됩니다.")
    @PostMapping("/year")
    public ResponseEntity<InputStreamResource> downloadYearlyReportExcelFile(
            HttpServletRequest request, Principal principal,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "조회할 기간의 시작일", example = "2022-10-01") LocalDate startDt,
            @RequestBody List<YearlyExcelDto> yearlyExcelDtoList)
        throws IOException {

        return excelService.downloadYearlyReportExcelFile(
            request, principal.getName(), startDt, yearlyExcelDtoList);
    }
}
