package com.rezero.inandout.diary.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddDiaryInput {
    @PastOrPresent(message = "일기작성일(과거 또는 현재)을 입력하세요.")
    private LocalDate diaryDt;

    private String text;
}
