import ReportMonthTabPanel from "./ReportMonthTabPanel";
import { act } from "@testing-library/react-hooks";
import { render, screen, waitFor, logRoles } from "@testing-library/react";
import * as reportUtil from "../../../utils/reportUtil";
import { useRef } from "react";
import axios from "axios";

const renderReportMonthTabPanel = () => {};
// initial props data
let tabValue = 0;
let graphTypeOptions = reportUtil.graphTypeOptions;
let graphTypeOption = reportUtil.graphTypeOptions[0].value;
let costOptions = reportUtil.costOptions;
let costOption = reportUtil.costOptions[0].value;
let handleCheckboxChange = jest.fn();
let currentMonth = new Date();
let rows = [];
let params = { startDt: "2022-12-01", endDt: "2022-12-31" };
let prevMonth = jest.fn();
let nextMonth = jest.fn();
let canvasRef;

describe("ReportMonthTabPanel", () => {
  beforeEach(async () => {
    const Wrapper = (props) => {
      canvasRef = useRef();
      return <>{props.children}</>;
    };

    axios.get = jest.fn().mockResolvedValue({ data: {} });

    const view = render(
      <Wrapper>
        <ReportMonthTabPanel
          tabValue={tabValue}
          graphTypeOptions={graphTypeOptions}
          graphTypeOption={graphTypeOption}
          costOptions={costOptions}
          costOption={costOption}
          handleCheckboxChange={handleCheckboxChange}
          currentMonth={currentMonth}
          rows={rows}
          dateParams={params}
          prevMonth={prevMonth}
          nextMonth={nextMonth}
          canvasRef={canvasRef}
        />
      </Wrapper>
    );

    // logRoles(view.container);
  });

  it("renders RadioButtonGroup Component", () => {
    expect(screen.queryByText(/막대/i)).not.toBeNull();
    expect(screen.queryByText(/파이/i)).not.toBeNull();
  });

  it("renders DateHeader Component", () => {
    expect(screen.queryByText(/월/i)).not.toBeNull();
  });

  it("renders Exceldownload text", () => {
    expect(screen.queryByText(/엑셀/i)).not.toBeNull();
  });
});
