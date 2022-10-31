package com.rezero.inandout.diary.controller;

import com.rezero.inandout.diary.model.AddDiaryInput;
import com.rezero.inandout.diary.service.DiaryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary")
public class DiaryController {

    private final DiaryService diaryService;

    @GetMapping
    @ApiOperation(value = "일기 목록 API",
            notes = "시작 날짜와 끝 날짜를 통해 일기 목록을 볼 수 있다.")
    public ResponseEntity<?> getDiaryList(Principal principal,
        @ApiParam(value = "조회 시작 날짜", example = "2022-01-01")
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate startDt,
        @ApiParam(value = "조회 끝 날짜", example = "2022-01-01")
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate endDt) {

        return ResponseEntity.ok(diaryService.getDiaryList(principal.getName(), startDt, endDt));

    }

    @PostMapping
    @ApiOperation(value = "일기 등록 API",
            notes = "일기 을 통해 저장을 할 수 있다.")
    public ResponseEntity<?> addDiary(Principal principal,
                                      @RequestPart AddDiaryInput addDiaryInput,
                                      @RequestPart MultipartFile file) {

        diaryService.addDiary(principal.getName(), addDiaryInput.getDiaryDt(),
                addDiaryInput.getText(), file);

        return ResponseEntity.ok("일기가 등록됐습니다.");
    }
}
