import React from "react";
import { useQuery } from "react-query";
import * as reportUtil from "../utils/reportUtil";
import { addMonths } from "date-fns";

function useReportQuery(
  costOption,
  tabValue,
  startMonth,
  yearlyOption,
  apiURL,
  params,
  monthGraph,
  yearGraph,
  setRows
) {
  const handleReportData = async (url, params) => {
    const fetchedData = await reportUtil.getReportDataFrom(url, params);

    setReportDataWith(fetchedData);
  };

  const setReportDataWith = (data) => {
    switch (tabValue) {
      case reportUtil.TabSelected.MONTH:
        const [newData, newLabel] = reportUtil.getMonthlyData(data);

        Object.keys(monthGraph).forEach((item) => {
          monthGraph[item].data.labels = newLabel;
          monthGraph[item].data.datasets[0].data = newData;
        });

        break;
      case reportUtil.TabSelected.YEAR:
        const yearLabel = [];
        let month = startMonth;
        for (let i = 1; i <= 12; i++) {
          const curMonth = month.getMonth();
          yearLabel.push(curMonth + 1);

          reportUtil.columns[i].key = reportUtil.months[curMonth];
          reportUtil.columns[i].name = `${curMonth + 1}월`;
          month = addMonths(month, 1);
        }

        Object.keys(yearGraph).forEach((item) => {
          yearGraph[item].data.labels = yearLabel;
        });

        const totalYearData = reportUtil.getTotalYearReportData(data);
        setRows(totalYearData);

        break;
      default:
        break;
    }
  };

  const { data, isLoading, refetch } = useQuery(
    [
      "getMonthOrYearReportData",
      costOption,
      tabValue,
      startMonth,
      yearlyOption,
    ],
    () => handleReportData(apiURL, params),
    {
      staleTime: 5 * (60 * 1000),
      // cacheTime: 1 * (60 * 1000),
      refetchOnWindowFocus: false,
    }
  );

  return { isLoading, refetch };
}

export default useReportQuery;
