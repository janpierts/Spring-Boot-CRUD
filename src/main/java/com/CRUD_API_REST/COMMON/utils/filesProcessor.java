package com.CRUD_API_REST.COMMON.utils;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class filesProcessor {
    public static final int NAME_COLUMN_INDEX = 0; 
    public static final int OTHER_FIELD_COLUMN_INDEX = 1;

    public static <T> List<T> excelToEntities(MultipartFile file,Function<Row, T> rowMapper) throws IOException {
        
        List<T> entityList = new ArrayList<>();

        try (InputStream is = file.getInputStream(); 
            Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row currentRow = sheet.getRow(i);
                
                if (currentRow == null) {
                    continue; 
                }

                T entity = rowMapper.apply(currentRow);
                
                if (entity != null) {
                    entityList.add(entity);
                }
            }
        }
        return entityList;
    }
    public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> null;
        };
    }
}
