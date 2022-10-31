package com.rezero.inandout.diary.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDto {

    private Long diaryId;
    private String nickName;
    private LocalDate diaryDt;
    private String text;
    private String s3ImageUrl;
}
