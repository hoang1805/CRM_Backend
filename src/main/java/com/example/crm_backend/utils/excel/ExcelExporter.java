package com.example.crm_backend.utils.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ExcelExporter {
    public static ByteArrayResource generateFile(List<String> columns, List<List<Object>> rows) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Account");

            // Tạo style cho header (nền đen, chữ trắng)
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Tạo style cho dữ liệu (bình thường, không có nền đen)
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Ghi hàng tiêu đề
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));
                cell.setCellStyle(headerStyle);
            }

            // Ghi dữ liệu
            for (int i = 0; i < rows.size(); i++) {
                Row currentRow = sheet.createRow(i + 1);
                List<Object> row = rows.get(i);

                for (int j = 0; j < row.size(); j++) {
                    Cell cell = currentRow.createCell(j);
                    Object value = row.get(j);

                    // Xử lý null để tránh lỗi
                    if (value == null) {
                        cell.setCellValue(""); // Để ô trống nếu giá trị null
                    } else {
                        cell.setCellValue(value.toString());
                    }

                    cell.setCellStyle(dataStyle);
                }
            }


            // Tự động chỉnh kích thước cột sau khi ghi toàn bộ dữ liệu
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi dữ liệu vào stream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close(); // Đóng workbook để giải phóng tài nguyên

            return new ByteArrayResource(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
