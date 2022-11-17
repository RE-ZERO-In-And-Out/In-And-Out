package com.rezero.inandout.calendar.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarExpenseDto {
    LocalDate expenseDt;

    String item;
    int amount;
}
