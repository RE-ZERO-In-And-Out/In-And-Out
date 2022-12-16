import { Grid, Box, Button } from "@mui/material";
import TabPanel from "../TabPanel";
import RadioButton from "../RadioButton";
import DateHeader from "../../common/DateHeader";
import ChartCanvas from "../ChartCanvas";
import axios from "axios";

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

export default function ReportMonthTabPanel({
  tabValue,
  graphTypeOptions,
  graphTypeOption,
  costOptions,
  costOption,
  handleCheckboxChange,
  currentMonth,
  prevMonth,
  nextMonth,
  canvasRef,
}) {
  return (
    <TabPanel value={tabValue} index={0}>
      <Grid display="flex" justifyContent="flex-end" sx={{ mb: 5, mt: -5 }}>
        <RadioButton
          legend={"차트"}
          buttonOptions={graphTypeOptions}
          checkedOption={graphTypeOption}
          buttonName={"monthly"}
          labelPlacement={"end"}
          handleChange={handleCheckboxChange}
        />

        <RadioButton
          legend={"항목"}
          buttonOptions={costOptions}
          checkedOption={costOption}
          buttonName={"cost"}
          labelPlacement={"end"}
          handleChange={handleCheckboxChange}
        />
      </Grid>
      <DateHeader
        type={"month"}
        currentTime={currentMonth}
        prev={prevMonth}
        next={nextMonth}
      />
      <ChartCanvas width={1000} height={500} ref={canvasRef} />
      <Box
        sx={{
          mt: 10,
          display: "flex",
          width: "100%",
          justifyContent: "flex-end",
        }}
      >
        {costOption === "income" && (
          <Button onClick={() => downloadToExcel("income")}>
            엑셀 다운로드 (수입)
          </Button>
        )}
        {costOption === "expense" && (
          <Button onClick={() => downloadToExcel("expense")}>
            엑셀 다운로드 (지출)
          </Button>
        )}
      </Box>
    </TabPanel>
  );
}
