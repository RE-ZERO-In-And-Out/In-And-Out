package com.rezero.inandout.diary.service;

import com.rezero.inandout.diary.model.DiaryDto;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface DiaryService {
    List<DiaryDto> getDiaryList(String email, LocalDate startDt, LocalDate endDt);

    void addDiary(String email, LocalDate diaryDt, String text, MultipartFile file);

    void updateDiary(String email, Long diaryId, LocalDate diaryDt, String text, MultipartFile file);

    void deleteDiary(String email, Long diaryId);
}
