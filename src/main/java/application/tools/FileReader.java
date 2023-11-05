package application.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FileReader {
    public static String getCellValue(String _xlsxFilePath, int _columnIndex, int _rowIndex)
            throws FileNotFoundException, IOException, Exception {

        String res = "";

        try (FileInputStream excelFile = new FileInputStream(_xlsxFilePath);
                Workbook workbook = new XSSFWorkbook(excelFile)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(_rowIndex - 1);
            if (row != null) {
                Cell cell = row.getCell(_columnIndex);
                if (cell != null) {
                    String cellValue = cell.getStringCellValue().trim();
                    res = cellValue;
                }
            }
        }
        return res;
    }
}
