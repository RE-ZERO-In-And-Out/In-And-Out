package com.rezero.inandout.diary.model;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateDiaryInput {
    @NotNull(message = "일기아이디를 입력하세요.")
    @Min(value = 1, message = "일기아이디는 1이상입니다.")
    private Long diaryId;
    @PastOrPresent(message = "일기작성일(과거 또는 현재)을 입력하세요.")
    private LocalDate diaryDt;

    private String text;
}
