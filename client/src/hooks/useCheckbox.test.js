import useCheckbox from "./useCheckbox";
import { act } from "@testing-library/react-hooks";
import { render } from "@testing-library/react";
import * as reportUtil from "../utils/reportUtil";

describe("useCheckbox", () => {
  let result;

  beforeEach(() => {
    // arrange
    const Wrapper = () => {
      result = useCheckbox();
    };
    render(<Wrapper />);
  });

  it("initially returns bar, chart, income text", () => {
    // assert
    expect(result.graphTypeOption).toBe("bar");
    expect(result.yearlyOption).toBe("chart");
    expect(result.costOption).toBe("income");
  });

  it("changes graphTypeOption to doughnut graph", () => {
    // act
    act(() => {
      result.setCheckboxMap["monthly"]({
        target: reportUtil.graphTypeOptions[1],
      });
    });

    // assert
    expect(result.graphTypeOption).toBe(reportUtil.graphTypeOptions[1].value);
  });
  it("changes yearlyOption to table", () => {
    // act
    act(() => {
      result.setCheckboxMap["yearly"]({ target: reportUtil.yearlyOptions[1] });
    });

    // assert
    expect(result.yearlyOption).toBe(reportUtil.yearlyOptions[1].value);
  });
  it("changes costOption to outcome", () => {
    // act
    act(() => {
      result.setCheckboxMap["cost"]({ target: reportUtil.costOptions[1] });
    });

    // assert
    expect(result.costOption).toBe(reportUtil.costOptions[1].value);
  });
});
