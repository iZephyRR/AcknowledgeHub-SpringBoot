package com.echo.acknowledgehub.util;

import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.constant.Gender;
import com.echo.acknowledgehub.entity.Employee;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class ExcelReader {
    private static final Logger LOGGER = Logger.getLogger(ExcelReader.class.getName());
    private final ModelMapper MAPPER;

    private ExcelReader(ModelMapper mapper) {
        this.MAPPER = mapper;
    }

    public List<Employee> getEmployees(FileInputStream xlsxFile) {

        List<Employee> employees = new ArrayList<>();
        try {
            Workbook workbook = new XSSFWorkbook(xlsxFile);
            List<String> columnNames = new ArrayList<>();
            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet
            for (Row row : sheet) {
                if (row == sheet.getRow(0)) {
                    columnNames = printCellValue(row).stream().map(source -> this.MAPPER.map(source, String.class)).toList();
                } else {
                    List<String> finalColumnNames = columnNames;
                    Employee finalEmployee = new Employee();
                    List<Object> finalObjects = printCellValue(row);
                    IntStream.range(0, columnNames.size())
                            .forEach(index -> {
                                switch (finalColumnNames.get(index).trim().toLowerCase().replaceAll(" ", "").replaceAll("-", "").replaceAll("_", "")) {
                                    case "stuffid":
                                        finalEmployee.setStuffId((String) finalObjects.get(index));
                                        break;
                                    case "telegramusername":
                                        finalEmployee.setTelegramUsername((String) finalObjects.get(index));
                                        break;
                                    case "email":
                                        finalEmployee.setEmail((String) finalObjects.get(index));
                                        break;
                                    case "nrc":
                                        finalEmployee.setNRC((String) finalObjects.get(index));
                                        break;
                                    case "name":
                                        finalEmployee.setName((String) finalObjects.get(index));
                                        break;
                                    case "position","rank":
                                        finalEmployee.setRole(
                                                switch (finalObjects.get(index).toString().trim().toLowerCase().replaceAll(" ", "").replaceAll("-", "").replaceAll("_", "")) {
                                                    case "admin" -> EmployeeRole.ADMIN;
                                                    case "mainhr" -> EmployeeRole.MAIN_HR;
                                                    case "mainhrassistance" -> EmployeeRole.MAIN_HR_ASSISTANCE;
                                                    case "hr" -> EmployeeRole.HR;
                                                    case "hrassistance" -> EmployeeRole.HR_ASSISTANCE;
                                                    default -> EmployeeRole.STUFF;
                                                }
                                        );
                                        break;
                                    case "dateofbirth":
                                        finalEmployee.setDob((Date) finalObjects.get(index));
                                        break;
                                    case "entrydate":
                                        finalEmployee.setWorkEntryDate((Date) finalObjects.get(index));
                                        break;
                                    case "gender":
                                        finalEmployee.setGender(
                                                switch (finalObjects.get(index).toString().trim().toLowerCase().replaceAll(" ", "").replaceAll("-", "").replaceAll("_", "")) {
                                                    case "male" -> Gender.MALE;
                                                    case "female" -> Gender.FEMALE;
                                                    default -> Gender.CUSTOM;
                                                }
                                        );
                                    case "address":
                                        finalEmployee.setAddress((String) finalObjects.get(index));
                                        break;
                                }
                            });
                    employees.add(finalEmployee);
                }
            }
            return employees;
        } catch (IOException e) {
            LOGGER.severe("xlsx to users converter error" + e.getMessage());
            return null;
        }
    }

    private List<Object> printCellValue(Row row) {
        List<Object> cells = new ArrayList<>();
        for (Cell cell : row) {
            switch (cell.getCellType()) {
                case STRING:
                    cells.add(cell.getStringCellValue());
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cells.add(cell.getDateCellValue());
                    } else {
                        cells.add(cell.getNumericCellValue());
                    }
                    break;
                case BOOLEAN:
                    cells.add(cell.getBooleanCellValue());
                    break;
                case FORMULA:
                    cells.add(cell.getCellFormula());
                    break;
                case BLANK:
                    cells.add("BLANK");
                    break;
                default:
                    cells.add("UNKNOWN");
                    break;
            }
        }
        return cells;
    }

    private static void image() {
        String excelFilePath = "C:/OJT-14/Final Project/excel_import_test.xlsx";

        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (Sheet sheet : workbook) {
                // for (Shape drawing : sheet.getDrawingPatriarch()) {
                List<? extends PictureData> pictures = workbook.getAllPictures();
                for (PictureData picture : pictures) {
                    if (picture instanceof XSSFPictureData) {
                        byte[] data = picture.getData();
                        String extension = picture.suggestFileExtension();

                        try (FileOutputStream fos = new FileOutputStream("image." + extension)) {
                            fos.write(data);
                            LOGGER.warning("Image : " + fos);
                        }
                    }
                }
                // }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
