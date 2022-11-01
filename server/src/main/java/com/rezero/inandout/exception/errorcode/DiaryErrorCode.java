package com.rezero.inandout.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DiaryErrorCode {

    THIS_DATE_EXIST_DIARY("이 날짜에는 일기가 존재합니다."),
    NOT_EXIST_DIARY("존재하지 않는 일기 입니다.");

    private final String description;
}
