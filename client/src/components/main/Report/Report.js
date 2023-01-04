import { useRef, useState, useEffect, useCallback } from "react";
import "react-data-grid/lib/styles.css";
import Grid from "@mui/material/Unstable_Grid2/Grid2";

import {
  addMonths,
  subMonths,
  subYears,
  startOfMonth,
  endOfMonth,
  format,
} from "date-fns";

import ReportTab from "./ReportTab";
import axios from "axios";
import { useQuery } from "react-query";
import PacmanLoader from "react-spinners/PacmanLoader";
import { monthGraph, yearGraph } from "../../../utils/graphOptions";

import ReportMonthTabPanel from "./ReportMonthTabPanel";
import ReportYearTabPanel from "./ReportYearTabPanel";
import * as reportUtil from "../../../utils/reportUtil";
import useCheckbox from "../../../hooks/useCheckbox";

const TabSelected = Object.freeze({
  MONTH: 0,
  YEAR: 1,
});

let currentMonth = new Date();

export default function Report() {
  const canvasRef = useRef(null);

  const { graphTypeOption, yearlyOption, costOption, setCheckboxMap } =
    useCheckbox();
  const [tabValue, setTabValue] = useState(0);

  const [rows, setRows] = useState([]);
  const [category, setCategory] = useState("");
  const [startMonth, setStartMonth] = useState(new Date());

  const handleDropdownCategoryChange = useCallback((event) => {
    reportUtil.setYearGraphDropdownCategoryData(
      yearGraph.lineConfig,
      reportUtil.categoryRows,
      event.target.value
    );
    setCategory(event.target.value);
  }, []);

  const setParamAndRefetch = () => {
    setParam();
    refetch();
  };

  const prevMonth = () => {
    currentMonth = subMonths(currentMonth, 1);
    setParamAndRefetch();
  };
  const nextMonth = () => {
    currentMonth = addMonths(currentMonth, 1);
    setParamAndRefetch();
  };

  const handleCheckboxChange = useCallback(
    (e) => {
      if (e.target.checked) {
        setCheckboxMap[e.target.name](e);
      }
    },
    [setCheckboxMap]
  );

  const setParam = () => {
    switch (tabValue) {
      case TabSelected.MONTH:
        params.startDt = reportUtil.formatDate(startOfMonth(currentMonth));
        params.endDt = reportUtil.formatDate(endOfMonth(currentMonth));
        break;
      case TabSelected.YEAR:
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

  const setReportDataWith = (data) => {
    switch (tabValue) {
      case TabSelected.MONTH:
        const [newData, newLabel] = reportUtil.getMonthlyData(data);

        Object.keys(monthGraph).forEach((item) => {
          monthGraph[item].data.labels = newLabel;
          monthGraph[item].data.datasets[0].data = newData;
        });

        break;
      case TabSelected.YEAR:
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

  const handleTabChange = useCallback((event, newValue) => {
    setTabValue(newValue);
  }, []);

  useEffect(() => {
    let charId;
    if (canvasRef.current) {
      charId = reportUtil.drawChart(
        canvasRef.current,
        tabValue === TabSelected.MONTH
          ? graphTypeOption === "bar"
            ? monthGraph.barConfig
            : monthGraph.doughnutConfig
          : yearGraph.lineConfig
      );

      canvasRef.current.onclick = function (evt) {
        const points = charId.getElementsAtEventForMode(
          evt,
          "nearest",
          { intersect: true },
          true
        );

        if (points.length) {
          const firstPoint = points[0];
          const label = charId.data.labels[firstPoint.index];
          const slabel = charId.data.datasets[firstPoint.datasetIndex].label;
          const value =
            charId.data.datasets[firstPoint.datasetIndex].data[
              firstPoint.index
            ];
          console.log(label, slabel, value);
        }
      };
    }
    return () => {
      charId && charId.destroy();
    };
  });

  let API_URL =
    tabValue === TabSelected.MONTH
      ? `${process.env.REACT_APP_API_URL}/api/report/month/${costOption}`
      : `${process.env.REACT_APP_API_URL}/api/report/year`;
  const params = {};
  setParam();

  const handleReportData = async (url, params) => {
    const fetchedData = await reportUtil.getReportDataFrom(url, params);

    setReportDataWith(fetchedData);
  };

  const { data, isLoading, refetch } = useQuery(
    [
      "getMonthOrYearReportData",
      costOption,
      tabValue,
      startMonth,
      yearlyOption,
    ],
    () => handleReportData(API_URL, params),
    { staleTime: 0, cacheTime: 0, refetchOnWindowFocus: false }
  );

  if (isLoading)
    return (
      <PacmanLoader
        style={{
          position: "fixed",
          top: "50%",
          left: "50%",
          transform: "translate(-50%, -50%)",
        }}
        color="#36d7b7"
        size={50}
      />
    );

  return (
    <Grid container spacing={0}>
      <ReportTab tabValue={tabValue} handleTabChange={handleTabChange} />
      <ReportMonthTabPanel
        tabValue={tabValue}
        graphTypeOptions={reportUtil.graphTypeOptions}
        graphTypeOption={graphTypeOption}
        costOptions={reportUtil.costOptions}
        costOption={costOption}
        handleCheckboxChange={handleCheckboxChange}
        currentMonth={currentMonth}
        rows={rows}
        dateParams={params}
        prevMonth={prevMonth}
        nextMonth={nextMonth}
        canvasRef={canvasRef}
      />

      <ReportYearTabPanel
        tabValue={tabValue}
        yearlyOptions={reportUtil.yearlyOptions}
        yearlyOption={yearlyOption}
        handleCheckboxChange={handleCheckboxChange}
        startMonth={startMonth}
        setStartMonth={setStartMonth}
        columns={reportUtil.columns}
        rows={rows}
        categoryRows={reportUtil.categoryRows}
        category={category}
        canvasRef={canvasRef}
        handleDropdownCategoryChange={handleDropdownCategoryChange}
      />
    </Grid>
  );
}
