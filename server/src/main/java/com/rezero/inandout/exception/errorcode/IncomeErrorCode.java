package com.rezero.inandout.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IncomeErrorCode {
    NO_INCOME("없는 수입내역입니다."),
    NO_MEMBER("없는 멤버입니다."),
    NO_CATEGORY("없는 카테고리입니다."),
    NOT_MATCH_MEMBER_AND_INCOME("수입내역의 주인이 아닙니다. 잘못된 요청입니다.");

    private final String description;

}
