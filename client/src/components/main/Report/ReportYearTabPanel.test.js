import ReportYearTabPanel from "./ReportYearTabPanel";
import {
  act,
  render,
  screen,
  waitFor,
  logRoles,
  fireEvent,
} from "@testing-library/react";
import * as reportUtil from "../../../utils/reportUtil";
import { useRef } from "react";
import axios from "axios";
import useCheckbox from "../../../hooks/useCheckbox";

const renderReportMonthTabPanel = () => {};
// initial props data
let tabValue = 1;
let yearlyOpt;
let startMonth = new Date();
let setStartMonth = jest.fn();
let rows = [];
let category = "";
let params = { startDt: "2022-12-01", endDt: "2022-12-31" };
let canvasRef;
let handleCheckboxChange;

let handleDropdownCategoryChange = jest.fn();

describe("ReportYearTabPanel", () => {
  beforeEach(async () => {
    const Wrapper = (props) => {
      canvasRef = useRef();
      const { graphTypeOption, yearlyOption, costOption, setCheckboxMap } =
        useCheckbox();

      const handleCheckboxChange = (e) => {
        if (e.target.checked) {
          setCheckboxMap[e.target.name](e);
        }
      };
      return (
        <>
          <ReportYearTabPanel
            tabValue={tabValue}
            yearlyOptions={reportUtil.yearlyOptions}
            yearlyOption={yearlyOption}
            handleCheckboxChange={handleCheckboxChange}
            startMonth={startMonth}
            setStartMonth={setStartMonth}
            columns={reportUtil.columns}
            rows={rows}
            dateParams={params}
            categoryRows={reportUtil.categoryRows}
            category={category}
            canvasRef={canvasRef}
            handleDropdownCategoryChange={handleDropdownCategoryChange}
          />
        </>
      );
    };

    axios.get = jest.fn().mockResolvedValue({ data: {} });

    const view = render(<Wrapper></Wrapper>);

    // logRoles(view.container);
  });

  // it("renders RadioButtonGroup Component", () => {
  //   expect(screen.queryByText(/막대/i)).not.toBeNull();
  //   expect(screen.queryByText(/파이/i)).not.toBeNull();
  // });

  it("renders checkbox items", () => {
    expect(screen.getAllByTestId("RadioButtonCheckedIcon")).not.toHaveLength(0);
    expect(screen.getAllByTestId("RadioButtonUncheckedIcon")).not.toHaveLength(
      0
    );
  });

  describe("when table option is selected", () => {
    it("renders Exceldownload text", () => {
      const radio = screen.getByLabelText(/표/i);

      fireEvent.click(radio);
      expect(screen.queryByText(/엑셀/i)).not.toBeNull();
    });
  });

  describe("when graph option is selected", () => {
    it("renders select menu", () => {
      const radio = screen.getByLabelText(/그래프/i);

      fireEvent.click(radio);

      expect(
        screen.getByTestId("year-category-select-label")
      ).toBeInTheDocument();
    });
  });
});
