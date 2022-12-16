import { useRef, useState, useEffect } from "react";
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

import {
  doughnutConfig,
  barConfig,
  lineConfig,
} from "../../../utils/graphOptions";

import ReportMonthTabPanel from "./ReportMonthTabPanel";
import ReportYearTabPanel from "./ReportYearTabPanel";
import {
  drawChart,
  columns,
  months,
  graphTypeOptions,
  yearlyOptions,
  costOptions,
} from "../../../utils/reportUtil";

const TabSelected = Object.freeze({
  MONTH: 0,
  YEAR: 1,
});

let currentMonth = new Date();
let categoryRows = [];

const setYearGraphDropdownCategoryData = (selectedCategory) => {
  let newData = [];
  categoryRows.forEach((row) => {
    if (row.category === selectedCategory) {
      newData = Object.entries(row)
        .slice(1, 13)
        .map((entry) => entry[1]);
      lineConfig.data.datasets[0].data = newData;
    }
  });
};

export default function Report() {
  const canvasRef = useRef(null);

  const [graphTypeOption, setgraphTypeOption] = useState("bar");
  const [yearlyOption, setYearlyOption] = useState("chart");
  const [costOption, setCostOption] = useState("income");
  const [tabValue, setTabValue] = useState(0);

  const [rows, setRows] = useState([]);
  const [category, setCategory] = useState("");
  const [startMonth, setStartMonth] = useState(new Date());

  const handleDropdownCategoryChange = (event) => {
    setYearGraphDropdownCategoryData(event.target.value);
    setCategory(event.target.value);
  };

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

  const checkboxMap = {
    monthly(e) {
      setgraphTypeOption(e.target.value);
    },
    yearly(e) {
      setYearlyOption(e.target.value);
    },
    cost(e) {
      setCostOption(e.target.value);
    },
  };

  const handleCheckbox = (checkboxType, e) => {
    checkboxMap[checkboxType](e);
  };

  const handleCheckboxChange = (e) => {
    if (e.target.checked) {
      handleCheckbox(e.target.name, e);
    }
  };

  const getMonthlyData = (fetchedData) => {
    const newData = [];
    const newLabel = [];
    console.log(fetchedData);
    fetchedData.sort((a, b) => b.categorySum - a.categorySum);
    fetchedData.forEach((element, idx) => {
      newData[idx] = Math.round(element.categoryRatio * 100);
      newLabel[idx] = `${element.category} - ${element.categorySum}`;
    });

    return [newData, newLabel];
  };

  const formatDate = (date) => {
    return `${date.getFullYear()}-${(date.getMonth() + 1)
      .toString()
      .padStart(2, "0")}-${date.getDate().toString().padStart(2, "0")}`;
  };

  const setParam = () => {
    switch (tabValue) {
      case TabSelected.MONTH:
        params.startDt = formatDate(startOfMonth(currentMonth));
        params.endDt = formatDate(endOfMonth(currentMonth));
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

  const getReportDataFrom = async (url, params) => {
    try {
      const res = await axios(
        `${url}?endDt=${params.endDt}&startDt=${params.startDt}`,
        {
          withCredentials: true,
        }
      );
      console.log(res);
      return res.data;
    } catch (err) {
      console.log(err);
    }
  };

  const createMainRows = (categoryTitle) => {
    let obj = {};
    columns.forEach((column) => {
      if (column.key === "category") obj[column.key] = categoryTitle;
      else obj[column.key] = 0;
    });

    return obj;
  };

  const renderTotalYearReportOnTableAndGraph = (data) => {
    console.log(data);

    const yearlyIncomeMonthSums = data.incomeReportList.map(
      (data) => data.monthlySum
    );

    const yearlyExpenseMonthSums = data.expenseReportList.map(
      (data) => data.monthlySum
    );

    const yearlyTotalMonthSums = yearlyIncomeMonthSums.map(
      (x, y) => x - yearlyExpenseMonthSums[y]
    );

    lineConfig.data.datasets[0].data = yearlyTotalMonthSums;

    let incomeCategories = {};
    let expenseCategories = {};
    const tempRows = [];

    tempRows.push(createMainRows("수입지출합계"));
    tempRows.push(createMainRows("수입합계"));

    const yearlyIncomeReport = data.incomeReportList.map((data) =>
      data.incomeReport ? data.incomeReport : 0
    );

    yearlyIncomeReport.forEach((report, idx) => {
      if (report.length !== 0) {
        for (let i = 0; i < report.length; i++) {
          if (!incomeCategories[report[i].category])
            incomeCategories[report[i].category] = new Array(14).fill(0);
          incomeCategories[report[i].category][idx + 1] = report[i].categorySum;
        }
      }
    });

    for (let key in incomeCategories) {
      let idx = 1;
      let sum = 0;
      const row = {};
      for (let item of columns) {
        if (item.key === "category") row[item.key] = key;
        else if (item.key === "sum") {
          row[item.key] = sum;
          tempRows[1][item.key] += row[item.key];
        } else {
          row[item.key] = incomeCategories[key][idx++];
          tempRows[1][item.key] += row[item.key];
          sum += row[item.key];
        }
      }
      tempRows.push(row);
    }

    tempRows.push(createMainRows("지출합계"));
    let expenseSumRowPos = tempRows.length - 1;

    const yearlyExpenseReport = data.expenseReportList.map((data) =>
      data.expenseReport ? data.expenseReport : 0
    );

    yearlyExpenseReport.forEach((report, idx) => {
      if (report.length !== 0) {
        for (let i = 0; i < report.length; i++) {
          if (!expenseCategories[report[i].category])
            expenseCategories[report[i].category] = new Array(14).fill(0);
          expenseCategories[report[i].category][idx + 1] =
            report[i].categorySum;
        }
      }
    });

    for (let key in expenseCategories) {
      let idx = 1;
      let sum = 0;
      const row = {};
      for (let item of columns) {
        if (item.key === "category") row[item.key] = key;
        else if (item.key === "sum") {
          row[item.key] = sum;
          tempRows[expenseSumRowPos][item.key] += row[item.key];
        } else {
          row[item.key] = expenseCategories[key][idx++];
          tempRows[expenseSumRowPos][item.key] += row[item.key];
          sum += row[item.key];
        }
      }
      tempRows.push(row);
    }

    const obj = {};
    for (const property in tempRows[1]) {
      obj[property] =
        tempRows[1][property] - tempRows[expenseSumRowPos][property];
    }
    obj.category = "수입지출합계";
    tempRows[0] = obj;
    console.log(tempRows);
    categoryRows = tempRows.slice();
    setRows(tempRows);
  };

  const setReportDataWith = (data) => {
    switch (tabValue) {
      case TabSelected.MONTH:
        const [newData, newLabel] = getMonthlyData(data);
        console.log(newData, newLabel);
        doughnutConfig.data.labels = newLabel;
        doughnutConfig.data.datasets[0].data = newData;
        console.log(newData);
        barConfig.data.labels = newLabel;
        barConfig.data.datasets[0].data = newData;
        break;
      case TabSelected.YEAR:
        const yearLabel = [];
        let month = startMonth;
        for (let i = 1; i <= 12; i++) {
          const curMonth = month.getMonth();
          yearLabel.push(curMonth + 1);

          columns[i].key = months[curMonth];
          columns[i].name = `${curMonth + 1}월`;
          month = addMonths(month, 1);
        }

        lineConfig.data.labels = yearLabel;

        renderTotalYearReportOnTableAndGraph(data);

        break;
      default:
        break;
    }
  };

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  useEffect(() => {
    let charId;
    if (canvasRef.current) {
      charId = drawChart(
        canvasRef.current,
        tabValue === TabSelected.MONTH
          ? graphTypeOption === "bar"
            ? barConfig
            : doughnutConfig
          : lineConfig
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
    const fetchedData = await getReportDataFrom(url, params);

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
        graphTypeOptions={graphTypeOptions}
        graphTypeOption={graphTypeOption}
        costOptions={costOptions}
        costOption={costOption}
        handleCheckboxChange={handleCheckboxChange}
        currentMonth={currentMonth}
        prevMonth={prevMonth}
        nextMonth={nextMonth}
        canvasRef={canvasRef}
      />

      <ReportYearTabPanel
        tabValue={tabValue}
        yearlyOptions={yearlyOptions}
        yearlyOption={yearlyOption}
        handleCheckboxChange={handleCheckboxChange}
        startMonth={startMonth}
        setStartMonth={setStartMonth}
        columns={columns}
        rows={rows}
        categoryRows={categoryRows}
        category={category}
        canvasRef={canvasRef}
        handleDropdownCategoryChange={handleDropdownCategoryChange}
      />
    </Grid>
  );
}
