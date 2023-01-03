import React, { useState } from "react";

function useCheckbox() {
  const [graphTypeOption, setgraphTypeOption] = useState("bar");
  const [yearlyOption, setYearlyOption] = useState("chart");
  const [costOption, setCostOption] = useState("income");

  const setCheckboxMap = {
    monthly(e) {
      setgraphTypeOption(e.target.value);
    },
    yearly(e) {
      setYearlyOption(e.target.value);
    },
    cost(e) {
      setCostOption(e.target.value);
    },
  };

  return { graphTypeOption, yearlyOption, costOption, setCheckboxMap };
}

export default useCheckbox;
