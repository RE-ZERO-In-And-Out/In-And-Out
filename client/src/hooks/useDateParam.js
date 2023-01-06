import React, { useState, useEffect } from "react";
import * as reportUtil from "../utils/reportUtil";

import {
  addMonths,
  subMonths,
  subYears,
  startOfMonth,
  endOfMonth,
  format,
} from "date-fns";

function useDateParam(tabValue, currentMonth, params) {
  const [startMonth, setStartMonth] = useState(new Date());

  useEffect(() => {
    setDateParamByTabValue(tabValue, currentMonth);
  });

  const setDateParamByTabValue = (tabValue, currentMonth) => {
    switch (tabValue) {
      case reportUtil.TabSelected.MONTH:
        params.startDt = reportUtil.formatDate(startOfMonth(currentMonth));
        params.endDt = reportUtil.formatDate(endOfMonth(currentMonth));
        break;
      case reportUtil.TabSelected.YEAR:
        const startYear = format(startMonth, "yyyy");
        const startMon = startMonth;
        const startDay = format(startOfMonth(startMon), "dd");
        const endYear = format(subYears(startMonth, 1), "yyyy");
        const endMon = subMonths(startMonth, 1);
        const endDay = format(endOfMonth(endMon), "dd");

        params.endDt = `${startYear}-${format(endMon, "MM")}-${endDay}`;
        params.startDt = `${endYear}-${format(startMon, "MM")}-${startDay}`;

        break;
      default:
        break;
    }
  };
  return { startMonth, setStartMonth, setDateParamByTabValue };
}

export default useDateParam;
