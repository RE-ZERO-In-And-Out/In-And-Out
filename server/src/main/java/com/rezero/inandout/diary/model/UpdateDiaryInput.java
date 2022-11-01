package com.rezero.inandout.diary.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateDiaryInput {
    @NotBlank
    private Long diaryId;
    @NotBlank
    private LocalDate diaryDt;

    private String text;
}
