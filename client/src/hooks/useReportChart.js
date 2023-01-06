import React, { useEffect } from "react";
import * as reportUtil from "../utils/reportUtil";
import { monthGraph, yearGraph } from "../utils/graphOptions";

function useReportChart(canvasRef, tabValue, graphTypeOption) {
  useEffect(() => {
    let charId;
    if (canvasRef.current) {
      charId = reportUtil.drawChart(
        canvasRef.current,
        tabValue === reportUtil.TabSelected.MONTH
          ? graphTypeOption === "bar"
            ? monthGraph.barConfig
            : monthGraph.doughnutConfig
          : yearGraph.lineConfig
      );

      canvasRef.current.onclick = function (evt) {
        const points = charId.getElementsAtEventForMode(
          evt,
          "nearest",
          { intersect: true },
          true
        );

        if (points.length) {
          const firstPoint = points[0];
          const label = charId.data.labels[firstPoint.index];
          const slabel = charId.data.datasets[firstPoint.datasetIndex].label;
          const value =
            charId.data.datasets[firstPoint.datasetIndex].data[
              firstPoint.index
            ];
          console.log(label, slabel, value);
        }
      };
    }
    return () => {
      charId && charId.destroy();
    };
  });
  return {};
}

export default useReportChart;
