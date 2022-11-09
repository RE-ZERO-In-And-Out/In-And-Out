package com.rezero.inandout.diary.model;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteDiaryInput {
    @NotNull(message = "일기아이디를 입력하세요.")
    @Min(value = 1, message = "일기아이디는 1이상입니다.")
    private Long diaryId;
}
