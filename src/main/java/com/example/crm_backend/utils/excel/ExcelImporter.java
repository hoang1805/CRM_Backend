package com.example.crm_backend.utils.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.jdbc.Work;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelImporter {
    public static String EXCEL_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static boolean isExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), EXCEL_TYPE);
    }

    public static List<Map<String, String>> readFile(InputStream input_stream, List<String> columns, Map<String, Boolean> options, int limit) {
        List<Map<String, String>> list = new ArrayList<>();
        boolean ignore_error = false;
        if (options != null && options.containsKey("ignore_error")) {
            ignore_error = options.get("ignore_error");
        }

        try (Workbook workbook = WorkbookFactory.create(input_stream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new RuntimeException("Invalid sheet");
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề
                try {
                    Map<String, String> rowData = readRow(row, columns, ignore_error);
                    if (!rowData.isEmpty()) {
                        list.add(rowData);
                    } else {
                        list.add(null);
                    }
                } catch (Exception e) {
                    if (ignore_error) {
                        list.add(null);
                    }
                    throw new RuntimeException(e);
                }

                if (list.size() >= limit) {
                    return list;
                }

            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file excel: " + e.getMessage());
        }

        return list;
    }

    private static Map<String, String> readRow(Row row, List<String> columns, boolean ignoreError) {
        Map<String, String> rowData = new HashMap<>();

        for (int i = 0; i < columns.size(); i++) {
            String column = columns.get(i);
            Cell cell = row.getCell(i);
            String value = getCellValue(cell);

//            if (value == null && !ignoreError) {
//                throw new RuntimeException("Thiếu dữ liệu tại cột: " + column + " (Hàng: " + row.getRowNum() + ")");
//            }

            if (value == null || value.isEmpty()) {
                value = "";
            }

            rowData.put(column, value);
        }
        return rowData;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return null;

        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);

//        return switch (cell.getCellType()) {
//            case STRING -> cell.getStringCellValue().trim();
//            case NUMERIC -> {
//                if (DateUtil.isCellDateFormatted(cell)) {
//                    yield new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
//                }
//                yield String.valueOf(cell.getNumericCellValue());
//            }
//            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
//            default -> null;
//        };
    }

    public static ByteArrayResource generateTemplate(List<String> columns) {
        try(Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Account");

            // Tạo style cho header (nền đen, chữ trắng)
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Ghi hàng tiêu đề
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i); // Tự động chỉnh độ rộng cột
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return new ByteArrayResource(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
