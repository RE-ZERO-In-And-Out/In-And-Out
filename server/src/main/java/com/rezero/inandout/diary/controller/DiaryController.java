package com.rezero.inandout.diary.controller;

import com.rezero.inandout.diary.model.AddDiaryInput;
import com.rezero.inandout.diary.model.DeleteDiaryInput;
import com.rezero.inandout.diary.model.DiaryDto;
import com.rezero.inandout.diary.model.UpdateDiaryInput;
import com.rezero.inandout.diary.service.DiaryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/diary")
public class DiaryController {

    private final DiaryService diaryService;

    @GetMapping
    @ApiOperation(value = "일기 목록 API",
            notes = "시작 날짜와 끝 날짜를 통해 일기 목록을 볼 수 있다.")
    public ResponseEntity<List<DiaryDto>> getDiaryList(Principal principal,
                                                       @ApiParam(value = "조회 시작 날짜", example = "2022-01-01")
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate startDt,
                                                       @ApiParam(value = "조회 끝 날짜", example = "2022-01-01")
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate endDt) {

        return ResponseEntity.ok(diaryService.getDiaryList(principal.getName(), startDt, endDt));

    }

    @PostMapping
    @ApiOperation(value = "일기 등록 API",
            notes = "일기날짜, 텍스트, 사진을 통해 저장을 할 수 있다.")
    public ResponseEntity<String> addDiary(Principal principal,
                                      @RequestPart @Valid AddDiaryInput input,
                                      MultipartFile file) {

        diaryService.addDiary(principal.getName(), input.getDiaryDt(),
                input.getText(), file);

        return ResponseEntity.ok("일기가 등록됐습니다.");
    }

    @PutMapping
    @ApiOperation(value = "일기 수정 API",
            notes = "일기날짜, 텍스트, 사진을 통해 수정을 할 수 있다.")
    public ResponseEntity<String> updateDiary(Principal principal,
                                      @RequestPart @Valid UpdateDiaryInput input,
                                      MultipartFile file) {

        diaryService.updateDiary(principal.getName(), input.getDiaryId(), input.getDiaryDt(),
                input.getText(), file);

        return ResponseEntity.ok("일기가 수정됐습니다.");
    }

    @DeleteMapping
    @ApiOperation(value = "일기 삭제 API",
            notes = "일기 ID를 통해 삭제 할 수 있다.")
    public ResponseEntity<String> deleteDiary(Principal principal,
                                         @RequestBody @Valid DeleteDiaryInput input) {

        diaryService.deleteDiary(principal.getName(), input.getDiaryId());

        return ResponseEntity.ok("일기가 삭제됐습니다.");
    }
}
