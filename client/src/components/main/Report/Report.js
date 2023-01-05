import { useRef, useState, useEffect, useCallback } from "react";
import "react-data-grid/lib/styles.css";
import Grid from "@mui/material/Unstable_Grid2/Grid2";

import { addMonths, subMonths } from "date-fns";

import ReportTab from "./ReportTab";

import PacmanLoader from "react-spinners/PacmanLoader";
import { monthGraph, yearGraph } from "../../../utils/graphOptions";

import ReportMonthTabPanel from "./ReportMonthTabPanel";
import ReportYearTabPanel from "./ReportYearTabPanel";
import * as reportUtil from "../../../utils/reportUtil";
import useCheckbox from "../../../hooks/useCheckbox";
import useDateParam from "../../../hooks/useDateParam";
import useReportChart from "../../../hooks/useReportChart";
import useReportQuery from "../../../hooks/useReportQuery";

const TabSelected = Object.freeze({
  MONTH: 0,
  YEAR: 1,
});

let currentMonth = new Date();
const params = {};

export default function Report() {
  const canvasRef = useRef(null);

  const { graphTypeOption, yearlyOption, costOption, setCheckboxMap } =
    useCheckbox();
  const [tabValue, setTabValue] = useState(0);
  const { startMonth, setStartMonth, setDateParamByTabValue } = useDateParam(
    tabValue,
    currentMonth,
    params
  );
  const {} = useReportChart(canvasRef, tabValue, graphTypeOption);

  const [rows, setRows] = useState([]);
  const [category, setCategory] = useState("");
  let API_URL =
    tabValue === TabSelected.MONTH
      ? `${process.env.REACT_APP_API_URL}/api/report/month/${costOption}`
      : `${process.env.REACT_APP_API_URL}/api/report/year`;

  const { isLoading, refetch } = useReportQuery(
    costOption,
    tabValue,
    startMonth,
    yearlyOption,
    API_URL,
    params,
    monthGraph,
    yearGraph,
    setRows
  );

  const handleDropdownCategoryChange = useCallback((event) => {
    reportUtil.setYearGraphDropdownCategoryData(
      yearGraph.lineConfig,
      reportUtil.categoryRows,
      event.target.value
    );
    setCategory(event.target.value);
  }, []);

  const setParamAndRefetch = () => {
    setDateParamByTabValue(tabValue, currentMonth);
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

  const handleTabChange = useCallback((event, newValue) => {
    setTabValue(newValue);
  }, []);

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
