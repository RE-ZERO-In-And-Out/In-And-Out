package com.rezero.inandout.diary.model;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteDiaryInput {
    @NotBlank
    private Long diaryId;
}
