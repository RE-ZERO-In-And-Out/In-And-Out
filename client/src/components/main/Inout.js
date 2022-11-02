import React, { useState } from "react";

import "react-data-grid/lib/styles.css";
import DataGrid, { SelectColumn, textEditor } from "react-data-grid";
import dropDownEditor from "../../editor/dropDownEditor";
import DateEditor from "../../editor/DateEditor";

import axios from "axios";

import { startOfMonth, endOfMonth, addMonths, subMonths } from "date-fns";

import DateHeader from "../common/DateHeader";

import Stack from "@mui/material/Stack";
import Button from "@mui/material/Button";

import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Unstable_Grid2/Grid2";

import TabPanel from "./TabPanel";

import { useQuery, useMutation, useQueryClient } from "react-query";
import PacmanLoader from "react-spinners/PacmanLoader";

const INCOME_API_URL = "http://localhost:5000/income";
const EXPENSE_API_URL = "http://localhost:5000/expense";

const incomeColumns = [
  SelectColumn,
  {
    key: "incomeDate",
    name: "날짜",
    width: 200,
    formatter(props) {
      return <>{props.row.date}</>;
    },
    editor: DateEditor,
  },
  {
    key: "incomeItem",
    name: "사용내역",
    editor: textEditor,
  },
  {
    key: "incomeAmount",
    name: "금액",
    editor: textEditor,
  },
  {
    key: "incomeCategoryName",
    name: "분류",
    formatter(props) {
      return <>{props.row.category}</>;
    },
    resizable: true,
    editor: dropDownEditor,
    editorOptions: {
      editOnClick: true,
    },
  },
  {
    key: "incomeMemo",
    name: "메모",
    width: 500,
    editor: textEditor,
  },
];

const expenseColumns = [
  SelectColumn,
  {
    key: "expenseDate",
    name: "날짜",
    width: 200,
    formatter(props) {
      return <>{props.row.date}</>;
    },
    editor: DateEditor,
  },
  {
    key: "expenseItem",
    name: "사용내역",
    editor: textEditor,
  },
  {
    key: "expenseCash",
    name: "현금",
    editor: textEditor,
  },
  {
    key: "expenseCard",
    name: "카드",
    editor: textEditor,
  },
  {
    key: "expenseCategoryName",
    name: "분류",
    formatter(props) {
      return <>{props.row.category}</>;
    },
    resizable: true,
    editor: dropDownEditor,
    editorOptions: {
      editOnClick: true,
    },
  },
  {
    key: "expenseMemo",
    name: "메모",
    width: 500,
    editor: textEditor,
  },
];

function a11yProps(index) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
}

const TabSelected = Object.freeze({
  INCOME: 0,
  EXPENSE: 1,
});

let currentMonth = new Date();

export default function Inout() {
  const [rows, setRows] = useState([]); // 나중에 빈배열로 처리
  const [selectedRows, setSelectedRows] = useState(() => new Set());
  const queryClient = useQueryClient();
  const [tabValue, setTabValue] = useState(0);

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const setParam = () => {
    params.start_dt = startOfMonth(currentMonth);
    params.end_dt = endOfMonth(currentMonth);
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

  async function getInoutDataFrom(url, params) {
    try {
      const res = await axios(url, { params: params });

      return res.data;
    } catch (err) {
      console.log(err);
    }
  }

  const setInoutDataWith = (data) => {
    switch (tabValue) {
      case TabSelected.INCOME:
        setRows(data.income);
        break;
      case TabSelected.EXPENSE:
        setRows(data.expense);
        break;
      default:
        break;
    }
  };

  function rowKeyGetter(row) {
    const id = tabValue === TabSelected.INCOME ? row.incomeId : row.expenseId;
    return id;
  }

  function createNewRow() {
    const newIncomeData = {
      incomeId: rows[rows.length - 1].incomeId + 1,
      incomeDate: "",
      incomeItem: "",
      incomeAmount: "",
      incomeCategoryName: "",
      incomeMemo: "",
    };

    const newExpenseData = {
      expenseId: rows[rows.length - 1].expenseId + 1,
      expenseDate: "",
      expenseItem: "",
      expenseCash: "",
      expenseCard: "",
      expenseCategoryName: "",
      expenseMemo: "",
    };
    let newData =
      tabValue === TabSelected.INCOME ? newIncomeData : newExpenseData;

    setRows([...rows, newData]);
  }

  const saveDataMutation = useMutation(
    async (rowData) => {
      const data =
        tabValue === TabSelected.INCOME
          ? { income: rowData }
          : { expense: rowData };
      const api =
        tabValue === TabSelected.INCOME ? INCOME_API_URL : EXPENSE_API_URL;
      try {
        const res = await axios.post(api, data, {
          headers: { "Content-Type": "application/json" },
        });
        return res.data;
      } catch (err) {
        console.log(err);
      }
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries("getInoutData");
      },
    }
  );

  const deleteDataMutation = useMutation(
    async (rowData) => {
      const api =
        tabValue === TabSelected.INCOME ? INCOME_API_URL : EXPENSE_API_URL;
      try {
        const res = await axios.delete(api, rowData, {
          headers: { "Content-Type": "application/json" },
        });
        return res.data;
      } catch (err) {
        console.log(err);
      }
    },
    {
      onSuccess: () => {
        queryClient.invalidateQueries("getInoutData");
      },
    }
  );

  function onSaveData() {
    saveDataMutation.mutate(rows);
  }

  function onDeleteData() {
    console.log("deleted");

    deleteDataMutation.mutate(selectedRows);
    const newRows = rows.slice();
    const filteredRow = newRows.filter((row, idx) => {
      const id = tabValue === TabSelected.INCOME ? row.incomeId : row.expenseId;
      return !selectedRows.has(id);
    });
    console.log("filtered", filteredRow);
  }

  const handleInoutData = async (url, params) => {
    const fetchedData = await getInoutDataFrom(url, params);
    setInoutDataWith(fetchedData);
  };

  const params = {};
  setParam();
  const { isLoading, refetch } = useQuery(["getInoutData", tabValue], () => {
    handleInoutData(
      tabValue === TabSelected.INCOME ? INCOME_API_URL : EXPENSE_API_URL,
      params
    );
  });

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
    <div>
      <Grid container spacing={0}>
        <Grid xs={12}>
          <Box sx={{ width: "100%" }}>
            <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
              <Tabs
                value={tabValue}
                onChange={handleTabChange}
                aria-label="periodic report"
              >
                <Tab label="수입" {...a11yProps(0)} />
                <Tab label="지출" {...a11yProps(1)} />
              </Tabs>
            </Box>
          </Box>
        </Grid>
      </Grid>

      <TabPanel value={tabValue} index={0}>
        <DateHeader
          type={"month"}
          currentTime={currentMonth}
          prev={prevMonth}
          next={nextMonth}
        />
        <DataGrid
          columns={incomeColumns}
          rows={rows}
          rowGetter={(i) => rows[i]}
          rowKeyGetter={rowKeyGetter}
          rowsCount={rows.length}
          onRowsChange={setRows}
          onRowClick={(data) => {
            console.log(data);
          }}
          selectedRows={selectedRows}
          onSelectedRowsChange={setSelectedRows}
        />
      </TabPanel>

      <TabPanel value={tabValue} index={1}>
        <DateHeader
          type={"month"}
          currentTime={currentMonth}
          prev={prevMonth}
          next={nextMonth}
        />
        <DataGrid
          columns={expenseColumns}
          rows={rows}
          rowGetter={(i) => rows[i]}
          rowKeyGetter={rowKeyGetter}
          rowsCount={rows.length}
          onRowsChange={setRows}
          onRowClick={(data) => {
            console.log(data);
          }}
          selectedRows={selectedRows}
          onSelectedRowsChange={setSelectedRows}
        />
      </TabPanel>
      <Button variant="text" size="large" onClick={createNewRow}>
        +
      </Button>
      <Box
        sx={{
          display: "flex",
          justifyContent: "flex-end",
        }}
      >
        <Button variant="contained" onClick={onSaveData}>
          저장
        </Button>
        <Button variant="outlined" onClick={onDeleteData}>
          삭제
        </Button>
      </Box>
    </div>
  );
}