package com.rezero.inandout.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExpenseErrorCode {
    NO_EXPENSE("없는 지출내역입니다."),
    NO_MEMBER("없는 멤버입니다."),
    NO_CATEGORY("없는 카테고리 입니다."),
    NOT_MATCH_MEMBER_AND_EXPENSE("지출내역의 주인이 아닙니다. 잘못된 요청입니다.");

    private final String description;

}
