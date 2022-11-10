package com.rezero.inandout.diary.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddDiaryInput {
    @NotNull(message = "일기작성일을 입력하세요.")
    private LocalDate diaryDt;
    private String text;
}
