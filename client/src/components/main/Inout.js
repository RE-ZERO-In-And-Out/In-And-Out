import React, { useState } from "react";
import { Link } from "react-router-dom";

import "react-data-grid/lib/styles.css";
import DataGrid, { SelectColumn, textEditor } from "react-data-grid";
import dropDownEditor from "../../editor/dropDownEditor";
import DateEditor from "../../editor/DateEditor";

const columns = [
  SelectColumn,
  {
    key: "date",
    name: "날짜",
    width: 200,
    formatter(props) {
      return <>{props.row.date}</>;
    },
    editor: DateEditor,
  },
  {
    key: "history",
    name: "사용내역",
    editor: textEditor,
  },
  {
    key: "cash",
    name: "현금",
    editor: textEditor,
  },
  {
    key: "card",
    name: "카드",
    editor: textEditor,
  },
  {
    key: "category",
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
    key: "memo",
    name: "메모",
    editor: textEditor,
  },
];

let rowss = [
  {
    id: 1,
    date: "10/6/2022",
    history: "Lasagne",
    cash: "4000",
    card: "32000",
    category: "식비",
    memo: "good",
  },
];

function rowKeyGetter(row) {
  return row.id;
}

let num = 2;
export default function Inout() {
  const [rows, setRows] = useState(rowss);
  const [selectedRows, setSelectedRows] = useState(() => new Set());
  // console.log("rerender");
  function getNewData() {
    const newData = {
      id: num++,
      date: "",
      history: "",
      cash: "",
      card: "",
      category: "",
      memo: "",
    };
    setRows([...rows, newData]);
    console.log(selectedRows);
    rowss.push(newData); // 요렇게 하면 다른 화면 이동 후에도 저장가능한듯?
    // console.log(rows);
  }

  function saveData() {
    alert("saved");
  }
  function deleteData() {
    // console.log(rows);
    // console.log([...selectedRows]);
    const newRows = rows.slice();
    const filteredRow = newRows.filter((row, idx) => !selectedRows.has(row.id));
    setRows(filteredRow);
    rowss = filteredRow;
  }

  return (
    <div>
      <DataGrid
        columns={columns}
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

        // onCopy={handleCopy}
      />
      <button onClick={getNewData}>Add</button>
      <button onClick={saveData}>저장</button>
      <button onClick={deleteData}>삭제</button>
    </div>
  );
}