package com.rezero.inandout.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IncomeErrorCode {
    NO_INCOME("없는 수입내역입니다."),
    NO_MEMBER("없는 멤버입니다."),
    NO_CATEGORY(""),
    NOT_MATCH_MEMBER_AND_INCOME("");

    private final String description;

}
