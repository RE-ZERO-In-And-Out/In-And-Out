import {
  Grid,
  Button,
  Stack,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
} from "@mui/material";

import "react-data-grid/lib/styles.css";
import DataGrid from "react-data-grid";

import ChartCanvas from "../ChartCanvas";
import TabPanel from "../TabPanel";
import RadioButton from "../RadioButton";
import DateHeader from "../../common/DateHeader";
import DatePicker from "react-datepicker";
import { ko } from "date-fns/esm/locale";
import * as reportUtil from "../../../utils/reportUtil";

export default function ReportYearTabPanel({
  tabValue,
  yearlyOptions,
  yearlyOption,
  handleCheckboxChange,
  startMonth,
  setStartMonth,
  columns,
  rows,
  dateParams,
  categoryRows,
  category,
  canvasRef,
  handleDropdownCategoryChange,
}) {
  return (
    <TabPanel value={tabValue} index={1}>
      <Grid
        display="flex"
        justifyContent="flex-end"
        sx={{ mb: 5, mt: -5 }}
        alignItems="baseline"
      >
        <RadioButton
          legend={"형식"}
          buttonOptions={yearlyOptions}
          checkedOption={yearlyOption}
          buttonName={"yearly"}
          labelPlacement={"end"}
          handleChange={handleCheckboxChange}
        />
      </Grid>
      <DatePicker
        locale={ko}
        selected={startMonth}
        onChange={(date) => setStartMonth(date)}
        dateFormat="MM/yyyy"
        showMonthYearPicker
      />
      <DateHeader
        type={"year"}
        currentTime={startMonth}
        prev={setStartMonth}
        next={setStartMonth}
      />
      {yearlyOption === "table" && (
        <div>
          <DataGrid columns={columns} rows={rows} height={500} />
          <Stack direction="row" spacing={2}>
            <Button
              onClick={() =>
                reportUtil.downloadToExcel(
                  "year",
                  rows,
                  dateParams.startDt,
                  dateParams.endDt
                )
              }
            >
              엑셀 다운로드 (연간 보고서)
            </Button>
          </Stack>
        </div>
      )}
      {yearlyOption === "chart" && (
        <>
          <span>
            <FormControl sx={{ ml: 70, minWidth: 120 }}>
              <InputLabel
                data-testid="year-category-select-label"
                id="category-select-label"
              >
                카테고리
              </InputLabel>
              <Select
                labelId="category-select-label"
                id="category-select"
                value={category}
                label="Age"
                autoWidth
                onChange={handleDropdownCategoryChange}
              >
                {categoryRows.map((row) => {
                  return (
                    <MenuItem key={row.category} value={row.category}>
                      {row.category}
                    </MenuItem>
                  );
                })}
              </Select>
            </FormControl>
          </span>
          <div>
            <ChartCanvas width={1000} height={500} ref={canvasRef} />
          </div>
        </>
      )}
    </TabPanel>
  );
}
