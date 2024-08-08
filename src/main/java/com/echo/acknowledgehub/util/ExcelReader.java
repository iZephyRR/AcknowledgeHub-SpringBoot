package com.echo.acknowledgehub.util;

import com.echo.acknowledgehub.entity.Employee;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {
    //private final ModelMapper mapper;
//private ExcelReader(ModelMapper mapper){
//    this.mapper=mapper;
//}
    public static void main(String[] args) {
        String excelFilePath = "C:/OJT-14/Final Project/excel_import_test.xlsx";
        List<Employee> employees =new ArrayList<>();
        List<String> columnNames=new ArrayList<>();
        ModelMapper mapper=new ModelMapper();
        Employee employee;
        try {
            FileInputStream fis = new FileInputStream(excelFilePath);
            Workbook workbook = new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

            for (Row row : sheet) {
                if(row==sheet.getRow(0)){
                    columnNames=printCellValue(row).stream().map(source -> mapper.map(source, String.class)).toList();
                    //                   List<CheatSheetDTO> cheatSheetDTOs = cheatSheetEntities.stream()
                    //                          .map(source -> mapper.map(source, CheatSheetDTO.class)).collect(Collectors.toList());
                }else{
                    //employee=new Employee();
                    for(Object object:printCellValue(row)){
                        System.out.println("Object "+object);
                    }
                }
            }
            System.out.println("columns "+columnNames);
            System.out.println("users "+ employees);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Object> printCellValue(Row row) {
        List<Object> cells=new ArrayList<>();
        for (Cell cell : row) {
            switch (cell.getCellType()) {
                case STRING:
                    // System.out.print(cell.getStringCellValue() + "\t");
                    cells.add(cell.getStringCellValue());
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        //  System.out.print(cell.getDateCellValue() + "\t");
                        cells.add(cell.getDateCellValue());
                    } else {
                        // System.out.print(cell.getNumericCellValue() + "\t");
                        cells.add(cell.getNumericCellValue());
                    }
                    break;
                case BOOLEAN:
                    //  System.out.print(cell.getBooleanCellValue() + "\t");
                    cells.add(cell.getBooleanCellValue());
                    break;
                case FORMULA:
                    //  System.out.print(cell.getCellFormula() + "\t");
                    cells.add(cell.getCellFormula());
                    break;
                case BLANK:
                    //  System.out.print("BLANK\t");
                    cells.add("BLANK");
                    break;
                default:
                    //  System.out.print("UNKNOWN\t");
                    cells.add("UNKNOWN");
                    break;
            }
        }
        return cells;
    }
}
