package com.rezero.inandout.excel.component;

import com.rezero.inandout.excel.model.ExpenseExcelDto;
import com.rezero.inandout.excel.model.IncomeExcelDto;
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
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
                    0, 0, 0, headColumn.size()));

            Row headerRow = sheet.createRow(rowNo++);
            Font headFont = workbook.createFont();
                headFont.setBold(true);
            CellStyle headStyle = workbook.createCellStyle();
                headStyle.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
                headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headStyle.setFont(headFont);

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
                        0, 0, 0, headColumn.size()));

            Row headerRow = sheet.createRow(rowNo++);
            Font headFont = workbook.createFont();
                headFont.setBold(true);
            CellStyle headStyle = workbook.createCellStyle();
                headStyle.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
                headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headStyle.setFont(headFont);
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
                    row.createCell(6).setCellValue(item.getExpenseCash());
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
}
