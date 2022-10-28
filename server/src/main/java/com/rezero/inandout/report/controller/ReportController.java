package com.rezero.inandout.report.controller;

import com.rezero.inandout.report.model.ReportDto;
import com.rezero.inandout.report.service.ReportService;
import io.swagger.annotations.ApiParam;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;


    @GetMapping("/month/income")
    public ResponseEntity<?> getMonthlyIncomeReport(Principal principal,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "조회할 기간의 시작일", example = "2022-10-01") LocalDate startDt,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "조회할 기간의 마지막일", example = "2022-10-31") LocalDate endDt) {
        List<ReportDto> reportDtoList
            = reportService.getMonthlyIncomeReport(principal.getName(), startDt, endDt);

        return ResponseEntity.ok().body(reportDtoList);

    }

}
