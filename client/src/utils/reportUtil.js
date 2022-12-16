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

export {
  drawChart,
  columns,
  months,
  graphTypeOptions,
  yearlyOptions,
  costOptions,
};
