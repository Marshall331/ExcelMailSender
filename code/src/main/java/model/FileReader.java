package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Utility class to read cell values from an Excel file.
 */
public class FileReader {

    /**
     * Retrieves the value from a specific cell in the Excel file.
     *
     * @param _xlsxFilePath Path to the Excel file.
     * @param _columnIndex  Index of the column.
     * @param _rowIndex     Index of the row.
     * @return The value in the specified cell.
     * @throws FileNotFoundException If the Excel file is not found at the given path.
     * @throws IOException           If an I/O error occurs while reading the file.
     * @throws Exception             If any unexpected exception occurs during file processing.
     */
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