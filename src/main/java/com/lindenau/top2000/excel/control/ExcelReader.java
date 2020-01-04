package com.lindenau.top2000.excel.control;

import com.lindenau.top2000.config.control.ConfigLoader;
import com.lindenau.top2000.domain.entity.EntrySong;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelReader {

    ConfigLoader configLoader = new ConfigLoader();
    int lines = 0;

    public ExcelReader() throws IOException {
    }

    public int getLines() {
        return lines;
    }

    public List<EntrySong> readSongsFromExcel() {
        List<EntrySong> songs = new ArrayList<EntrySong>();
        try {
            File file = new File(configLoader.getExcelPath());
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> itr = sheet.iterator();
            itr.next();
            while (itr.hasNext()) {
                Row row = itr.next();
                String rowValue = readRow(row);
                System.out.println("Read excel row: " + rowValue);
                lines++;
                songs.add(SongBuilder.buildFromRow(rowValue));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songs;
    }

    private String readRow(Row row) {
        Iterator<Cell> cellIterator = row.cellIterator();
        StringBuilder rowValues = new StringBuilder();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            rowValues.append(readCell(cell)).append(";");
        }
        String value = rowValues.toString();
        checkSemicolons(value);
        return value.substring(0, value.length() - 1);
    }

    private String readCell(Cell cell) {
        CellType type = cell.getCellType();
        String value = "";
        switch (type) {
            case STRING:
                value = cell.getStringCellValue();
                break;
            case NUMERIC:
                value = String.valueOf((int) cell.getNumericCellValue());
                break;
        }
        return value;
    }

    private void checkSemicolons(String row) {
        char temp;
        int charCount = 0;
        for (int i = 0; i < row.length(); i++) {
            temp = row.charAt(i);
            if (temp == ';') {
                charCount++;
            }
        }
        if (charCount > 4) {
            System.out.println("----->>>> There is a problem with following entry, contains too many semicolons: " + row);
        }
    }
}
