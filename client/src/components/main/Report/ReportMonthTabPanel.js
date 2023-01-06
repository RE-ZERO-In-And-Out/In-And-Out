import { Grid, Box, Button } from "@mui/material";
import TabPanel from "../TabPanel";
import RadioButton from "../RadioButton";
import DateHeader from "../../common/DateHeader";
import ChartCanvas from "../ChartCanvas";
import * as reportUtil from "../../../utils/reportUtil";

export default function ReportMonthTabPanel({
  tabValue,
  graphTypeOptions,
  graphTypeOption,
  costOptions,
  costOption,
  handleCheckboxChange,
  currentMonth,
  rows,
  dateParams,
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
          <Button
            onClick={() =>
              reportUtil.downloadToExcel(
                "income",
                rows,
                dateParams.startDt,
                dateParams.endDt
              )
            }
          >
            엑셀 다운로드 (수입)
          </Button>
        )}
        {costOption === "expense" && (
          <Button
            onClick={() =>
              reportUtil.downloadToExcel(
                "expense",
                rows,
                dateParams.startDt,
                dateParams.endDt
              )
            }
          >
            엑셀 다운로드 (지출)
          </Button>
        )}
      </Box>
    </TabPanel>
  );
}
