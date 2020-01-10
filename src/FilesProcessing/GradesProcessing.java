package FilesProcessing;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GradesProcessing {

    private List<Map<String, String>> grades = new ArrayList<>();

    public void processGrades(File file) {

        try {
            List<String> columnNames = new ArrayList<>();
            FileInputStream excelFile = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            int rowNumber = 0;

            for (Row currentRow : datatypeSheet) {
                int columnNumber = 0;
                Map<String, String> grade = new HashMap<>();
                StringBuilder name = new StringBuilder();
                for (Cell currentCell : currentRow) {
                    if (rowNumber == 0) {
                        columnNames.add(currentCell.getStringCellValue());
                    }
                    else {
                        if (currentCell.getCellType() == CellType.STRING) {
                            if (columnNumber == 0) {
                                name.append(currentCell.getStringCellValue());
                                name.append(" ");
                            } else if (columnNumber == 1) {
                                name.append(currentCell.getStringCellValue());
                                grade.put("Name", String.valueOf(name));
                            } else if (columnNumber > 5) {
                                grade.put(columnNames.get(columnNumber), currentCell.getStringCellValue());
                            }
                        } else if (currentCell.getCellType() == CellType.NUMERIC) {
                            grade.put(columnNames.get(columnNumber), String.valueOf(currentCell.getNumericCellValue()));
                        }
                    }
                    columnNumber ++;
                }
                grades.add(grade);
                rowNumber ++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearAllGradeData() {
        grades = new ArrayList<>();
    }

    public List<Map<String, String>> getGrades() {
        return grades;
    }
}
