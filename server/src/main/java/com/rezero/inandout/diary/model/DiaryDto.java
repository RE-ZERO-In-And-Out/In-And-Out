package com.rezero.inandout.diary.model;

import com.rezero.inandout.diary.entity.Diary;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public static DiaryDto toDto(Diary diary) {
        return DiaryDto.builder()
                .diaryId(diary.getDiaryId())
                .nickName(diary.getMember().getNickName())
                .diaryDt(diary.getDiaryDt())
                .text(diary.getText())
                .diaryPhotoUrl(diary.getDiaryPhotoUrl())
                .build();
    }

    public static List<DiaryDto> toDtos(List<Diary> diaries) {
        List<DiaryDto> diaryDtos = new ArrayList<>();

        for (Diary diary : diaries) {
            diaryDtos.add(toDto(diary));
        }

        return diaryDtos;
    }
}
