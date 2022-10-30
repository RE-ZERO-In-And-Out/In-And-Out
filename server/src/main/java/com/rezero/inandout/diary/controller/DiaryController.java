package com.rezero.inandout.diary.controller;

import com.rezero.inandout.diary.model.DiaryDto;
import com.rezero.inandout.diary.service.DiaryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary")
public class DiaryController {

    private final DiaryService diaryService;

    @GetMapping
    @ApiOperation(value = "지출내역 목록 API",
            notes = "시작 날짜와 끝 날짜를 통해 지출내역 목록을 볼 수 있다.")
    public List<DiaryDto> getDiaryList(Principal principal,
       @ApiParam(value = "조회 시작 날짜", example = "2022-01-01")
       @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate startDt,
       @ApiParam(value = "조회 끝 날짜", example = "2022-01-01")
       @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate endDt) {

        return diaryService.getDiaryList(principal.getName(), startDt, endDt);

    }

}
