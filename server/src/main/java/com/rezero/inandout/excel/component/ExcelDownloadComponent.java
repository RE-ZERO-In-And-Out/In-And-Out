package com.rezero.inandout.excel.component;

import com.rezero.inandout.excel.model.ExpenseExcelDto;
import com.rezero.inandout.excel.model.IncomeExcelDto;
import com.rezero.inandout.excel.model.YearlyExcelDto;
import com.rezero.inandout.excel.service.impl.ExcelServiceImpl.ColConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelDownloadComponent {

    public ResponseEntity<InputStreamResource> downloadIncomeExcelFile(HttpServletRequest request,
        String email, LocalDate startDt, LocalDate endDt,
        List<IncomeExcelDto> excelRowData, ArrayList<ColConfig> headColumn)
        throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("수입내역");

            int rowNo = 0;
            Row dateRow = sheet.createRow(rowNo++);
                dateRow.createCell(0).setCellValue("조회기간: " + startDt + " ~ " + endDt);
                sheet.addMergedRegion(new CellRangeAddress(
                    0, 0, 0, headColumn.size() - 1));

            Row headerRow = sheet.createRow(rowNo++);
            Font headFont = workbook.createFont();
                headFont.setFontName("맑은 고딕");
                headFont.setBold(true);
            CellStyle headStyle = workbook.createCellStyle();
                headStyle.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
                headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headStyle.setBorderTop(BorderStyle.THICK);
                headStyle.setBorderBottom(BorderStyle.THICK);
                headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                headStyle.setFont(headFont);

            CellStyle numCellStyle = workbook.createCellStyle();
                numCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            for (int i = 0; i < headColumn.size(); i++) {
                    headerRow.createCell(i).setCellValue(headColumn.get(i).colName);
                    sheet.setColumnWidth(i, headColumn.get(i).colWidth);
                    headerRow.getCell(i).setCellStyle(headStyle);
            }

            int dataNum = 1;
            for (IncomeExcelDto item : excelRowData) {
                Row row = sheet.createRow(rowNo++);
                    row.createCell(0).setCellValue(dataNum++);
                    row.createCell(1).setCellValue(item.getIncomeDt()
                                            .format(DateTimeFormatter.ISO_LOCAL_DATE));
                    row.createCell(2).setCellValue(item.getIncomeCategoryName());
                    row.createCell(3).setCellValue(item.getDetailIncomeCategoryName());
                    row.createCell(4).setCellValue(item.getIncomeItem());
                    row.createCell(5).setCellValue(item.getIncomeAmount());
                    row.getCell(5).setCellStyle(numCellStyle);
                    row.createCell(6).setCellValue(item.getIncomeMemo());
            }

            File tmpFile = File.createTempFile("TMP~", ".xls");
            try (OutputStream fos = new FileOutputStream(tmpFile);) {
                workbook.write(fos);
            }
            InputStream res = new FileInputStream(tmpFile) {
                @Override
                public void close() throws IOException {
                    super.close();
                    if (tmpFile.delete()) {
                        log.info("임시 파일 삭제 완료");
                    }
                }
            };

            log.info("[Excel Export Income] member: " + email + " / period: " + startDt + " ~ " + endDt);

            return ResponseEntity.ok()
                .contentLength(tmpFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition",
                    "attachment;filename=income_" + email + ".xlsx")
                .body(new InputStreamResource(res));
        }
    }


    public ResponseEntity<InputStreamResource> downloadExpenseExcelFile(HttpServletRequest request,
        String email, LocalDate startDt, LocalDate endDt,
        List<ExpenseExcelDto> expenseExcelDtoList, ArrayList<ColConfig> headColumn)
        throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("지출내역");

            int rowNo = 0;
            Row dateRow = sheet.createRow(rowNo++);
                dateRow.createCell(0).setCellValue("조회기간: " + startDt + " ~ " + endDt);
                sheet.addMergedRegion(new CellRangeAddress(
                        0, 0, 0, headColumn.size() - 1));

            Row headerRow = sheet.createRow(rowNo++);
            Font headFont = workbook.createFont();
                headFont.setFontName("맑은 고딕");
                headFont.setBold(true);
            CellStyle headStyle = workbook.createCellStyle();
                headStyle.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
                headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headStyle.setBorderTop(BorderStyle.THICK);
                headStyle.setBorderBottom(BorderStyle.THICK);
                headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                headStyle.setFont(headFont);

            CellStyle numCellStyle = workbook.createCellStyle();
                numCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            for (int i = 0; i < headColumn.size(); i++) {
                headerRow.createCell(i).setCellValue(headColumn.get(i).colName);
                sheet.setColumnWidth(i, headColumn.get(i).colWidth);
                headerRow.getCell(i).setCellStyle(headStyle);
            }

            int itemNo = 1;
            for (ExpenseExcelDto item : expenseExcelDtoList) {
                Row row = sheet.createRow(rowNo++);
                    row.createCell(0).setCellValue(itemNo++);
                    row.createCell(1).setCellValue(item.getExpenseDt()
                        .format(DateTimeFormatter.ISO_LOCAL_DATE));
                    row.createCell(2).setCellValue(item.getExpenseCategoryName());
                    row.createCell(3).setCellValue(item.getDetailExpenseCategoryName());
                    row.createCell(4).setCellValue(item.getExpenseItem());
                    row.createCell(5).setCellValue(item.getExpenseCard());
                    row.getCell(5).setCellStyle(numCellStyle);
                    row.createCell(6).setCellValue(item.getExpenseCash());
                    row.getCell(6).setCellStyle(numCellStyle);
                    row.createCell(7).setCellValue(item.getExpenseMemo());
            }

            File tmpFile = File.createTempFile("TMP~", ".xls");
            try (OutputStream fos = new FileOutputStream(tmpFile);) {
                workbook.write(fos);
            }
            InputStream res = new FileInputStream(tmpFile) {
                @Override
                public void close() throws IOException {
                    super.close();
                    if (tmpFile.delete()) {
                        log.info("임시 파일 삭제 완료");
                    }
                }
            };

            log.info("[Excel Export Expense] member: " + email + " / period: " + startDt + " ~ " + endDt);

            return ResponseEntity.ok()
                .contentLength(tmpFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition",
                    "attachment;filename=expense_" + email + ".xlsx")
                .body(new InputStreamResource(res));
        }
    }

    public ResponseEntity<InputStreamResource> downloadYearlyReport(HttpServletRequest request,
        String email, LocalDate startDt, LocalDate endDt,
        List<YearlyExcelDto> yearlyExcelDtoList, ArrayList<ColConfig> headColumn)
        throws IOException {

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("연간수입지출보고서");

            int rowNo = 0;
            Row dateRow = sheet.createRow(rowNo++);
                dateRow.createCell(0).setCellValue("조회기간: " + startDt + " ~ " + endDt);
                sheet.addMergedRegion(new CellRangeAddress(
                0, 0, 0, headColumn.size() - 1));

            Row headerRow = sheet.createRow(rowNo++);
            Font headFont = workbook.createFont();
                headFont.setFontName("맑은 고딕");
                headFont.setBold(true);
            CellStyle headStyle = workbook.createCellStyle();
                headStyle.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
                headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headStyle.setBorderTop(BorderStyle.THICK);
                headStyle.setBorderBottom(BorderStyle.THICK);
                headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                headStyle.setFont(headFont);

            CellStyle numCellStyle = workbook.createCellStyle();
                numCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            for (int i = 0; i < headColumn.size(); i++) {
                headerRow.createCell(i).setCellValue(headColumn.get(i).colName);
                sheet.setColumnWidth(i, headColumn.get(i).colWidth);
                headerRow.getCell(i).setCellStyle(headStyle);
            }

            int monthVal = startDt.getMonthValue();

            for (YearlyExcelDto item : yearlyExcelDtoList) {
                Row row = sheet.createRow(rowNo++);
                    row.createCell(0).setCellValue(item.getCategory());
                    row.createCell(getColumnNum(1, monthVal)).setCellValue(item.getJan());
                    row.createCell(getColumnNum(2, monthVal)).setCellValue(item.getFeb());
                    row.createCell(getColumnNum(3, monthVal)).setCellValue(item.getMar());
                    row.createCell(getColumnNum(4, monthVal)).setCellValue(item.getApr());
                    row.createCell(getColumnNum(5, monthVal)).setCellValue(item.getMay());
                    row.createCell(getColumnNum(6, monthVal)).setCellValue(item.getJun());
                    row.createCell(getColumnNum(7, monthVal)).setCellValue(item.getJul());
                    row.createCell(getColumnNum(8, monthVal)).setCellValue(item.getAug());
                    row.createCell(getColumnNum(9, monthVal)).setCellValue(item.getSep());
                    row.createCell(getColumnNum(10, monthVal)).setCellValue(item.getOct());
                    row.createCell(getColumnNum(11, monthVal)).setCellValue(item.getNov());
                    row.createCell(getColumnNum(12, monthVal)).setCellValue(item.getDec());

                for (int i = 1; i <= 12; i++) {
                    row.getCell(i).setCellStyle(numCellStyle);
                }

                if(Objects.equals(item.getCategory(), "수입지출합계")) {

                    CellStyle sumStyle = workbook.createCellStyle();
                        sumStyle.setFillForegroundColor(HSSFColorPredefined.LIGHT_GREEN.getIndex());
                        sumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        sumStyle.setBorderTop(BorderStyle.THICK);
                        sumStyle.setBorderBottom(BorderStyle.THICK);
                        sumStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        sumStyle.setFont(headFont);
                        sumStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

                    for (int i = 0; i < 13; i++) {
                        row.getCell(i).setCellStyle(sumStyle);
                    }
                }

                if(Objects.equals(item.getCategory(), "수입합계") ||
                    Objects.equals(item.getCategory(), "지출합계" ) ) {

                    CellStyle sumStyle = workbook.createCellStyle();
                        sumStyle.setFillForegroundColor(HSSFColorPredefined.LIGHT_TURQUOISE.getIndex());
                        sumStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        sumStyle.setBorderTop(BorderStyle.THIN);
                        sumStyle.setBorderBottom(BorderStyle.THIN);
                        sumStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                        sumStyle.setFont(headFont);
                        sumStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

                    for (int i = 0; i < 13; i++) {
                        row.getCell(i).setCellStyle(sumStyle);
                    }
                }
            }

            File tmpFile = File.createTempFile("TMP~", ".xls");
            try (OutputStream fos = new FileOutputStream(tmpFile);) {
                workbook.write(fos);
            }
            InputStream res = new FileInputStream(tmpFile) {
                @Override
                public void close() throws IOException {
                    super.close();
                    if (tmpFile.delete()) {
                        log.info("임시 파일 삭제 완료");
                    }
                }
            };

            log.info("[Excel Export Yearly Report] member: " + email + " / period: " + startDt + " ~ " + endDt);

            return ResponseEntity.ok()
                .contentLength(tmpFile.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition",
                    "attachment;filename=yearly_report_" + email + ".xlsx")
                .body(new InputStreamResource(res));
        }
    }

    private static int getColumnNum(int colNum, int monthVal) {
        if(colNum >= monthVal) {
            return (colNum - monthVal + 14) % 13;
        } else {
            return (colNum - monthVal + 13) % 13;
        }
    }
}
