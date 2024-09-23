package com.echo.acknowledgehub.util;

import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.constant.Gender;
import com.echo.acknowledgehub.exception_handler.XlsxReaderException;
import com.echo.acknowledgehub.entity.Employee;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.stream.IntStream;

@AllArgsConstructor
@Component
public class XlsxReader {
    private static final Logger LOGGER = Logger.getLogger(XlsxReader.class.getName());
    private final ModelMapper MAPPER;

    @Async
    public CompletableFuture<List<Employee>> getEmployees(InputStream xlsxFile) {
        LOGGER.info("Starting xlsx convertor...");
        final List<Employee> EMPLOYEES = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(xlsxFile)) {
            LOGGER.info("Got workbook..");
            final AtomicReference<List<String>> COLUMN_NAMES = new AtomicReference<>();
            final Sheet SHEET = workbook.getSheetAt(0); // Get the first sheet
            LOGGER.info("Got first sheet..");
            for (Row row : SHEET) {
                if (row == SHEET.getRow(0)) {
                    final CompletableFuture<List<Object>> FIRST_ROW = printCellValue(row);
                    FIRST_ROW.thenAccept(objects -> {
                        COLUMN_NAMES.set(objects.stream().map(source -> this.MAPPER.map(source, String.class)).toList());
                    }).exceptionally(ex -> {
                        LOGGER.warning(ex.getMessage());
                        throw new XlsxReaderException();
                    });
                } else {
                    final List<String> FINAL_COLUMN_NAMES = COLUMN_NAMES.get();
                    final Employee EMPLOYEE = new Employee();
                    final CompletableFuture<List<Object>> ROW = printCellValue(row);
                    ROW.thenAccept(finalObjects -> {
                        IntStream.range(0, COLUMN_NAMES.get().size())
                                .forEach(index -> {
                                    switch (FINAL_COLUMN_NAMES.get(index).trim().toLowerCase().replaceAll(" ", "").replaceAll("-", "").replaceAll("_", "")) {
                                        case "stuffid":
                                            EMPLOYEE.setStaffId((String) finalObjects.get(index));
                                            break;
                                        case "telegramusername":
                                            EMPLOYEE.setTelegramUsername((String) finalObjects.get(index));
                                            break;
                                        case "email":
                                            EMPLOYEE.setEmail((String) finalObjects.get(index));
                                            break;
                                        case "nrc":
                                            EMPLOYEE.setNrc((String) finalObjects.get(index));
                                            break;
                                        case "name":
                                            EMPLOYEE.setName((String) finalObjects.get(index));
                                            break;
                                        case "position", "rank":
                                            EMPLOYEE.setRole(
                                                    switch (finalObjects.get(index).toString().trim().toLowerCase().replaceAll(" ", "").replaceAll("-", "").replaceAll("_", "")) {
                                                        case "admin" -> EmployeeRole.ADMIN;
                                                        case "mainhr" -> EmployeeRole.MAIN_HR;
                                                        case "mainhrassistance" -> EmployeeRole.MAIN_HR_ASSISTANCE;
                                                        case "hr" -> EmployeeRole.HR;
                                                        case "hrassistance" -> EmployeeRole.HR_ASSISTANCE;
                                                        default -> EmployeeRole.STAFF;
                                                    }
                                            );
                                            break;
                                        case "dateofbirth":
                                            EMPLOYEE.setDob((Date) finalObjects.get(index));
                                            break;
                                        case "address":
                                            EMPLOYEE.setAddress((String) finalObjects.get(index));
                                            break;
                                        case "gender":
                                            EMPLOYEE.setGender(
                                                    switch (finalObjects.get(index).toString().trim().toLowerCase().replaceAll(" ", "").replaceAll("-", "").replaceAll("_", "")) {
                                                        case "male" -> Gender.MALE;
                                                        case "female" -> Gender.FEMALE;
                                                        default -> Gender.CUSTOM;
                                                    }
                                            );
                                            break;
                                    }
                                });
                    }).exceptionally(ex -> {
                        LOGGER.warning(ex.getMessage());
                        throw new XlsxReaderException();
                    });
                    EMPLOYEES.add(EMPLOYEE);
                }
            }
            LOGGER.info("Finished process...");
            return CompletableFuture.completedFuture(EMPLOYEES);
        } catch (IOException e) {
            LOGGER.severe("xlsx to users converter error" + e.getMessage());
            throw new XlsxReaderException();
        } catch (Exception e) {
            LOGGER.severe("Global exception : " + e);
            throw new XlsxReaderException();
        }
    }

//    @Async
//    public CompletableFuture<List<Department>> getDepartments(InputStream xlsxFile) {
//        LOGGER.info("Starting xlsx convertor...");
//        final List<Department> DEPARTMENTS = new ArrayList<>();
//        try (Workbook workbook = new XSSFWorkbook(xlsxFile)) {
//            LOGGER.info("Got workbook..");
//            final AtomicReference<List<String>> COLUMN_NAMES = new AtomicReference<>();
//            final Sheet SHEET = workbook.getSheetAt(0); // Get the first sheet
//            LOGGER.info("Got first sheet..");
//            for (Row row : SHEET) {
//                if (row == SHEET.getRow(0)) {
//                    final CompletableFuture<List<Object>> FIRST_ROW = printCellValue(row);
//                    FIRST_ROW.thenAccept(objects -> {
//                        COLUMN_NAMES.set(objects.stream().map(source -> this.MAPPER.map(source, String.class)).toList());
//                    }).exceptionally(ex -> {
//                        LOGGER.warning(ex.getMessage());
//                        throw new XlsxReaderException();
//                    });
//                } else {
//                    final List<String> FINAL_COLUMN_NAMES = COLUMN_NAMES.get();
//                    final Department DEPARTMENT = new Department();
//                    final CompletableFuture<List<Object>> ROW = printCellValue(row);
//                    ROW.thenAccept(finalObjects -> {
//                        IntStream.range(0, COLUMN_NAMES.get().size())
//                                .forEach(index -> {
//                                    if (FINAL_COLUMN_NAMES.get(index).trim().toLowerCase().replaceAll(" ", "").replaceAll("-", "").replaceAll("_", "")) {
//
//                                    }
//                                });
//                    }).exceptionally(ex -> {
//                        LOGGER.warning(ex.getMessage());
//                        throw new XlsxReaderException();
//                    });
//                    DEPARTMENTS.add(DEPARTMENT);
//                }
//            }
//            LOGGER.info("Finished process...");
//            return CompletableFuture.completedFuture(DEPARTMENTS);
//        } catch (IOException e) {
//            LOGGER.severe("xlsx to users converter error" + e.getMessage());
//            throw new XlsxReaderException();
//        }catch (Exception e){
//            LOGGER.severe("Global exception : "+e);
//            throw new XlsxReaderException();
//        }
//    }

    @Async
    private CompletableFuture<List<Object>> printCellValue(Row row) {
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
        return CompletableFuture.completedFuture(cells);
    }

    @Async
    private CompletableFuture<Void> extractImages() {
        String excelFilePath = "C:/OJT-14/Final Project/excel_import_test.xlsx";

        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (Sheet sheet : workbook) {
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
            }
            return CompletableFuture.completedFuture(null);
        } catch (IOException e) {
            LOGGER.severe("Error extracting images: " + e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }
}
