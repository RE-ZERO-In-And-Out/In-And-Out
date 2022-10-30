package com.rezero.inandout.diary.service;

import com.rezero.inandout.diary.model.DiaryDto;

import java.time.LocalDate;
import java.util.List;

public interface DiaryService {
    List<DiaryDto> getDiaryList(String email, LocalDate startDt, LocalDate endDt);
}
