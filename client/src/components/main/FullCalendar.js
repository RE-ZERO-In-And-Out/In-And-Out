import React, { useState, useRef, useEffect } from "react";
import { Icon } from "@iconify/react";
import colors from "../../utils/color";

import {
  format,
  addMonths,
  subMonths,
  startOfMonth,
  endOfMonth,
  startOfWeek,
  endOfWeek,
  isSameMonth,
  isSameDay,
  addDays,
  parse,
} from "date-fns";

import styled from "styled-components";

import { calenderStore } from "../../store/store.js";

const InstanceTable = styled.div`
  margin-top: 25px;
  margin-left: 40px;
  min-width: 200px;
  min-height: 200px;
  max-height: 300px;
  max-width: 245px;
  // border: 1px solid black;
  background-color: rgba(248, 249, 215, 1);
  box-shadow: 5px 5px 5px 5px gray;
  position: absolute;
  overflow-y: scroll;
  overflow-x: hidden;

  cursor: default;
  &::-webkit-scrollbar {
    width: 5px;
  }
  &::-webkit-scrollbar-thumb {
    height: 20%;
    background: #c0c0c0;
    border-radius: 10px;
  }
`;

const InstanceTable2 = styled.div`
  margin-top: -200px;
  margin-left: 40px;
  min-width: 200px;
  min-height: 200px;
  max-height: 300px;
  max-width: 245px;
  // border: 1px solid black;
  background-color: rgba(248, 249, 215, 1);
  box-shadow: 5px 5px 5px 5px gray;
  position: absolute;
  overflow-y: scroll;
  overflow-x: hidden;
  cursor: default;
  &::-webkit-scrollbar {
    width: 5px;
  }
  &::-webkit-scrollbar-thumb {
    height: 20%;
    background: #c0c0c0;
    border-radius: 10px;
  }
`;

const TableWrapper = styled.div`
  position: relative;
`;

const CloseBtn = styled.div`
  position: sticky;
  top: 0;
  margin-left: 5px;
  height: 30px;
  font-size: 25px;
  cursor: pointer;
`;

const RenderHeader = (props) => {
  const { currentMonth, setcurrentMonth, tabMonth, setTabMonth } =
    calenderStore();

  const handleTabChange = (event, newValue) => {
    setTabMonth(newValue);
    // console.log(currentMonth);
  };

  const prevMonth = () => {
    props.changeValue(
      format(startOfMonth(subMonths(currentMonth, 1)), "yyyy-MM-dd"),
      format(endOfMonth(subMonths(currentMonth, 1)), "yyyy-MM-dd")
    );
    setcurrentMonth(subMonths(currentMonth, 1));
  };
  const nextMonth = () => {
    props.changeValue(
      format(startOfMonth(addMonths(currentMonth, 1)), "yyyy-MM-dd"),
      format(endOfMonth(addMonths(currentMonth, 1)), "yyyy-MM-dd")
    );
    setcurrentMonth(addMonths(currentMonth, 1));
  };

  const commaMaker = (input) => {
    input = input.toString();
    let counter = 0;
    let string = [];
    for (let i = input.length - 1; i >= 0; i--) {
      string.unshift(input[i]);
      counter++;
      if (counter === 3) {
        string.unshift(",");
        counter = 0;
      }
    }
    if (string[0] === ",") {
      string.shift();
    }
    string = string.join("");
    return string;
  };

  let a = commaMaker(props.calendarData.incomeSum);
  let b = commaMaker(props.calendarData.expenseSum);

  return (
    <div>
      <div style={{ width: "20px", height: "15px" }}></div>
      <div className="header row">
        <div className="col col-first">
          <span className="text">
            <span className="text month">{format(currentMonth, "M")}월</span>
            {format(currentMonth, "yyyy")}
          </span>
        </div>
        <div className="col col-center">
          <div
            style={{
              marginTop: "5px",
            }}
          >
            <div
              style={{
                fontWeight: "600",
                color: "rgba(109, 73,129, 1)",
                maxHeight: "20px",
              }}
            >
              in
            </div>
            <div
              style={{
                fontWeight: "600",
                color: "rgba(109, 73,129, 1)",
                maxHeight: "20px",
              }}
            >
              out
            </div>
          </div>
          <div style={{ marginTop: "5px", marginLeft: "10px" }}>
            <div style={{ maxHeight: "20px" }}>+{a}</div>
            <div style={{ maxHeight: "20px" }}>-{b}</div>
          </div>
        </div>
        <div className="col col-end">
          <Icon
            icon="bi:arrow-left-circle-fill"
            style={{ color: "#F2D7D9" }}
            value={tabMonth}
            onChange={handleTabChange}
            onClick={prevMonth}
          />
          <Icon
            icon="bi:arrow-right-circle-fill"
            style={{ color: "#F2D7D9" }}
            value={tabMonth}
            onChange={handleTabChange}
            onClick={nextMonth}
          />
        </div>
      </div>
    </div>
  );
};

const RenderDays = () => {
  const days = [];
  const date = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

  for (let i = 0; i < 7; i++) {
    days.push(
      <div className="col" key={i}>
        {date[i]}
      </div>
    );
  }

  return <div className="days row">{days}</div>;
};

const RenderCells = ({ diaryDatas, calendarData, X, Y }) => {
  const {
    showDiary,
    setShowDiary,
    setCalendarImage,
    showWrittenDiary,
    setShowWrittenDiary,
    diaryDate,
    setDiaryDate,
    setDetailDate,
    selectedDate,
    setSelectedDate,
    currentMonth,
    setCurrentMonth,
    showNewDiary,
    setShowNewDiary,
    specificDate,
    setSpecificDate,
    showInstanceTable,
    setShowInstanceTable,
    dateOrigin,
    setDateOrigin,
    setText,
    setDiaryImage,
    setDiaryId,
    position,
    setPosition,
  } = calenderStore();

  // const onDateClick = (day) => {
  //   console.log(1, day);
  //   setSelectedDate(day);
  // };

  // const tempDiaryData = diaryData.filter((data) => data.date === diaryDate)[0];

  const commaMaker = (input) => {
    input = input.toString();
    let counter = 0;
    let string = [];
    for (let i = input.length - 1; i >= 0; i--) {
      string.unshift(input[i]);
      counter++;
      if (counter === 3) {
        string.unshift(",");
        counter = 0;
      }
    }
    if (string[0] === ",") {
      string.shift();
    }
    string = string.join("");
    return string;
  };

  const tempDiaryData = diaryDatas;
  const tempDiaryData2 = calendarData;

  let expenseDT = [];

  let incomeDT = [];

  function formatter(e) {
    let a = e;
    let string = a[5] + a[6] + "/" + a[8] + a[9] + "/" + a[2] + a[3];
    return string;
  }

  if (tempDiaryData2.calendarExpenseDtoList.length) {
    tempDiaryData2.calendarExpenseDtoList.map((data) => {
      let a = data.expenseDt;
      let string = a[5] + a[6] + "/" + a[8] + a[9] + "/" + a[2] + a[3];
      expenseDT.push(string);
    });
  }

  // console.log(tempDiaryData2.calendarIncomeDtoList.length);

  if (tempDiaryData2.calendarIncomeDtoList.length) {
    tempDiaryData2.calendarIncomeDtoList.map((data) => {
      let a = data.incomeDt;
      let string = a[5] + a[6] + "/" + a[8] + a[9] + "/" + a[2] + a[3];
      incomeDT.push(string);
    });
  }

  // console.log(tempDiaryData2);
  // console.log(expenseDT);
  // console.log(incomeDT);

  let dateArr = expenseDT.concat(incomeDT);
  let set = new Set(dateArr);
  dateArr = Array.from(set);

  // console.log(dateArr[0].format("MM/dd/yy"));

  const monthStart = startOfMonth(currentMonth);
  const monthEnd = endOfMonth(monthStart);
  const startDate = startOfWeek(monthStart);
  const endDate = endOfWeek(monthEnd);

  const rows = [];
  let days = [];
  let day = startDate;
  let formattedDate = "";

  const fixPosition = () => {
    setPosition(Y);
  };

  // console.log(startDate.format("MM/dd/yy"));

  while (day <= endDate) {
    for (let i = 0; i < 7; i++) {
      formattedDate = format(day, "d");
      // console.log(day.format("yyyy-MM-dd"));
      const cloneDay = day.format("yyyy-MM-dd");
      const formattedDataForDiary = day.format("MM/dd/yy");
      days.push(
        <div
          className={`col cell ${
            !isSameMonth(day, monthStart)
              ? "disabled"
              : isSameDay(day, selectedDate)
              ? "selected"
              : format(currentMonth, "M") !== format(day, "M")
              ? "not-valid"
              : "valid"
          }`}
          key={formattedDataForDiary}
          // onClick={() => onDateClick(parse(cloneDay, "YYYYMMDD", new Date()))}
          style={{
            display: "flex",
            flexDirection: "column",
            justifyContent: "space-between",
            cursor: "pointer",
            backgroundColor:
              formattedDataForDiary === format(new Date(), "MM/dd/yy") &&
              "#FFE7CC",
          }}
          id={cloneDay}
          onClick={(e) => {
            if (
              cloneDay[5].toString() + cloneDay[6].toString() ==
              format(currentMonth, "MM")
            ) {
              setSpecificDate(formattedDataForDiary);
              setDiaryDate(formattedDataForDiary);
              setDateOrigin(e.target.id);
              setShowDiary(!showDiary);
            }
          }}
        >
          <span
            className={
              format(currentMonth, "M") !== format(day, "M")
                ? "text not-valid"
                : ""
            }
            key={cloneDay}
          >
            {formattedDate}
          </span>
          <div
            style={{
              width: "80%",
              marginLeft: "10px",
              marginBottom: "10px",
              display: "flex",
              justifyContent: "space-between",
            }}
            id={formattedDataForDiary}
          >
            {tempDiaryData.length > 0 &&
              tempDiaryData.map((data) => {
                if (formattedDataForDiary === formatter(data.diaryDt)) {
                  if (data.s3ImageUrl === "") {
                    return (
                      <Icon
                        icon="arcticons:diary"
                        id={formattedDataForDiary}
                        key={formatter(data.diaryDt)}
                      ></Icon>
                    );
                  } else {
                    return (
                      <Icon
                        style={{ color: "orange" }}
                        icon="arcticons:diary"
                        id={formattedDataForDiary}
                        key={formatter(data.diaryDt)}
                      ></Icon>
                    );
                  }
                } else {
                  return (
                    <div
                      style={{ display: "none" }}
                      key={formatter(data.diaryDt)}
                    ></div>
                  );
                }
              })}
            {dateArr.length > 0 &&
              dateArr.map((data) => {
                if (data == formattedDataForDiary) {
                  return (
                    <>
                      <Icon
                        icon="cil:magnifying-glass"
                        id={formattedDataForDiary}
                        key={data.date}
                        onClick={(e) => {
                          // console.log(diaryData);
                          e.stopPropagation();
                          console.log(Y);
                          fixPosition();
                          setDetailDate(e.target.id);
                          setShowInstanceTable(!showInstanceTable);
                          setDiaryDate(formattedDataForDiary);
                          // console.log(showInstanceTable);
                        }}
                      ></Icon>
                      {showInstanceTable &&
                        formattedDataForDiary === diaryDate &&
                        (position > 300 ? (
                          <InstanceTable2
                            // key={data.date}
                            onClick={(e) => {
                              e.stopPropagation();
                            }}
                          >
                            <CloseBtn
                              onClick={(e) => {
                                setShowInstanceTable(!showInstanceTable);
                              }}
                            >
                              &#215;
                            </CloseBtn>
                            <TableWrapper
                              style={{
                                display: "flex",
                                flexDirection: "column",
                                alignItems: "center",
                                justifyContent: "center",
                              }}
                            >
                              <h3 style={{ margin: "0px", padding: "0px" }}>
                                &lt;지출&gt;
                              </h3>
                              <div>
                                {tempDiaryData2.calendarExpenseDtoList.map(
                                  (expense) => {
                                    if (
                                      formatter(expense.expenseDt) == diaryDate
                                    ) {
                                      let amount = commaMaker(expense.amount);
                                      return (
                                        <div>
                                          <span>{expense.item}</span>
                                          <span>&nbsp; : &nbsp;</span>
                                          <span>{amount}</span>
                                        </div>
                                      );
                                    }
                                  }
                                )}
                              </div>
                              <h3 style={{ margin: "0px", padding: "0px" }}>
                                &lt;수입&gt;
                              </h3>
                              <div>
                                {tempDiaryData2.calendarIncomeDtoList.map(
                                  (income) => {
                                    if (
                                      formatter(income.incomeDt) == diaryDate
                                    ) {
                                      let amount = commaMaker(income.amount);
                                      return (
                                        <div>
                                          <span>{income.item}</span>
                                          <span>&nbsp; : &nbsp;</span>
                                          <span>{amount}</span>
                                        </div>
                                      );
                                    }
                                  }
                                )}
                              </div>
                            </TableWrapper>
                          </InstanceTable2>
                        ) : (
                          <InstanceTable
                            // key={data.date}
                            onClick={(e) => {
                              e.stopPropagation();
                            }}
                          >
                            <CloseBtn
                              onClick={(e) => {
                                setShowInstanceTable(!showInstanceTable);
                              }}
                            >
                              &#215;
                            </CloseBtn>
                            <TableWrapper
                              style={{
                                display: "flex",
                                flexDirection: "column",
                                alignItems: "center",
                                justifyContent: "center",
                              }}
                            >
                              <h3 style={{ margin: "0px", padding: "0px" }}>
                                &lt;지출&gt;
                              </h3>
                              <div>
                                {tempDiaryData2.calendarExpenseDtoList.map(
                                  (expense) => {
                                    if (
                                      formatter(expense.expenseDt) == diaryDate
                                    ) {
                                      let amount = commaMaker(expense.amount);
                                      return (
                                        <div>
                                          <span>{expense.item}</span>
                                          <span>&nbsp; : &nbsp;</span>
                                          <span>{amount}</span>
                                        </div>
                                      );
                                    }
                                  }
                                )}
                              </div>
                              <h3 style={{ margin: "0px", padding: "0px" }}>
                                &lt;수입&gt;
                              </h3>
                              <div>
                                {tempDiaryData2.calendarIncomeDtoList.map(
                                  (income) => {
                                    if (
                                      formatter(income.incomeDt) == diaryDate
                                    ) {
                                      let amount = commaMaker(income.amount);
                                      return (
                                        <div>
                                          <span>{income.item}</span>
                                          <span>&nbsp; : &nbsp;</span>
                                          <span>{amount}</span>
                                        </div>
                                      );
                                    }
                                  }
                                )}
                              </div>
                            </TableWrapper>
                          </InstanceTable>
                        ))}
                    </>
                  );
                } else {
                  return (
                    <div style={{ display: "none" }} key={data.date}></div>
                  );
                }
              })}
          </div>
        </div>
      );
      day = addDays(day, 1);
    }
    rows.push(
      <div className="row" key={day}>
        {days}
      </div>
    );
    days = [];
  }
  return <div className="body">{rows}</div>;
};

export const FullCalendar = ({
  diaryDatas,
  changeValue,
  calendarData,
  refetch,
  X,
  Y,
}) => {
  return (
    <div className="calendar">
      <RenderHeader
        refetch={refetch}
        calendarData={calendarData}
        changeValue={changeValue}
      />
      <RenderDays />
      <RenderCells
        X={X}
        Y={Y}
        diaryDatas={diaryDatas}
        calendarData={calendarData}
      />
    </div>
  );
};
