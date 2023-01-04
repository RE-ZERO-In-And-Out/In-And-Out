import {
  Chart,
  BarElement,
  BarController,
  CategoryScale,
  LinearScale,
  Title,
  Legend,
  DoughnutController,
  ArcElement,
  PieController,
  LineController,
  PointElement,
  LineElement,
} from "chart.js";
import ChartDataLabels from "chartjs-plugin-datalabels";

import axios from "axios";

import { monthGraph, yearGraph } from "./graphOptions";

Chart.register(
  BarElement,
  BarController,
  CategoryScale,
  LinearScale,
  Title,
  Legend,
  DoughnutController,
  ArcElement,
  PieController,
  ChartDataLabels,
  LineController,
  PointElement,
  LineElement
);

const graphTypeOptions = [
  { value: "bar", label: "막대형" },
  { value: "doughnut", label: "파이형" },
];
const yearlyOptions = [
  { value: "table", label: "표" },
  { value: "chart", label: "그래프" },
];
const costOptions = [
  { value: "income", label: "수입" },
  { value: "expense", label: "지출" },
];

const months = [
  "jan",
  "feb",
  "mar",
  "apr",
  "may",
  "jun",
  "jul",
  "aug",
  "sep",
  "oct",
  "nov",
  "dec",
];

let columns = [
  { key: "category", name: "내용", width: 200 },
  { key: "jan", name: "1월" },
  { key: "feb", name: "2월" },
  { key: "mar", name: "3월" },
  { key: "apr", name: "4월" },
  { key: "may", name: "5월" },
  { key: "jun", name: "6월" },
  { key: "jul", name: "7월" },
  { key: "aug", name: "8월" },
  { key: "sep", name: "9월" },
  { key: "oct", name: "10월" },
  { key: "nov", name: "11월" },
  { key: "dec", name: "12월" },
  { key: "sum", name: "합계" },
];

const drawChart = (ctx, config) => {
  return new Chart(ctx, config);
};

const downloadToExcel = async (type, dataRows, startDt, endDt) => {
  let url = "";
  switch (type) {
    case "income":
    case "expense":
      url = `${process.env.REACT_APP_API_URL}/api/excel/${type}?startDt=${startDt}&endDt=${endDt}`;
      break;
    case "year":
      url = `${process.env.REACT_APP_API_URL}/api/excel/${type}?startDt=${startDt}`;
      break;
    default:
      break;
  }

  try {
    const obj = {
      yearlyExcelDtoList: dataRows,
    };
    await axios(url, {
      method: type === "year" ? "POST" : "GET",
      data: type === "year" ? obj.yearlyExcelDtoList : {},
      responseType: "blob", // important
      withCredentials: true,
    }).then((response) => {
      console.log(response);
      const url = window.URL.createObjectURL(
        new Blob([response.data], { type: response.headers["content-type"] })
      );
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `${type}-report-${startDt}-${endDt}.xlsx`);
      document.body.appendChild(link);
      link.click();
      return response;
    });
  } catch (err) {
    console.log(err.response.data);
  }
};

let categoryRows = [];

const createMainRows = (categoryTitle) => {
  let obj = {};
  columns.forEach((column) => {
    if (column.key === "category") obj[column.key] = categoryTitle;
    else obj[column.key] = 0;
  });

  return obj;
};

const setYearGraphDropdownCategoryData = (
  graphConfig,
  categoryRows,
  selectedCategory
) => {
  let newData = [];
  categoryRows.forEach((row) => {
    if (row.category === selectedCategory) {
      newData = Object.entries(row)
        .slice(1, 13)
        .map((entry) => entry[1]);
      graphConfig.data.datasets[0].data = newData;
    }
  });
};

const getTotalYearReportData = (data) => {
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

  yearGraph.lineConfig.data.datasets[0].data = yearlyTotalMonthSums;

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
        expenseCategories[report[i].category][idx + 1] = report[i].categorySum;
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

  return tempRows;
};

export {
  drawChart,
  columns,
  months,
  graphTypeOptions,
  yearlyOptions,
  costOptions,
  downloadToExcel,
  setYearGraphDropdownCategoryData,
  categoryRows,
  getTotalYearReportData,
};
