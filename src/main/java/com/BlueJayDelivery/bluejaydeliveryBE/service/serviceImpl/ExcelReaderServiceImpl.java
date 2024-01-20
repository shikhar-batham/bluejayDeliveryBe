package com.BlueJayDelivery.bluejaydeliveryBE.service.serviceImpl;

import com.BlueJayDelivery.bluejaydeliveryBE.entity.ExcelReader;
import com.BlueJayDelivery.bluejaydeliveryBE.payload.EmployeeDto;
import com.BlueJayDelivery.bluejaydeliveryBE.repo.ExcelReaderRepo;
import com.BlueJayDelivery.bluejaydeliveryBE.service.ExcelReaderService;
import org.apache.poi.ss.usermodel.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ExcelReaderServiceImpl implements ExcelReaderService {

    @Autowired
    private ExcelReaderRepo excelReaderRepo;

    @Override
    public void readExcelFile(MultipartFile file) {

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {

                if (row.getRowNum() == 0) continue;

                Iterator<Cell> cellIterator = row.cellIterator();

                ExcelReader excelReader = new ExcelReader();

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    int index = cell.getColumnIndex();

                    if (index == 0) {
                        if (cell.getCellType() == CellType.STRING)
                            excelReader.setPositionId(cell.getStringCellValue());
                    } else if (index == 1) {
                        if (cell.getCellType() == CellType.STRING)
                            excelReader.setPositionStatus(cell.getStringCellValue());
                    } else if (index == 2) {
                        if (cell.getCellType() == CellType.NUMERIC) {
                            excelReader.setTime(String.valueOf(cell.getLocalDateTimeCellValue()));
                        }
                    } else if (index == 3) {
                        if (cell.getCellType() == CellType.NUMERIC) {
                            excelReader.setTimeOut(String.valueOf(cell.getLocalDateTimeCellValue()));
                        }
                    } else if (index == 4) {
                        excelReader.setTimeCardHours(cell.getStringCellValue());
                    } else if (index == 5) {
                        if (cell.getCellType() == CellType.NUMERIC)
                            excelReader.setPayCycleStartDate(String.valueOf(cell.getLocalDateTimeCellValue()));
                    } else if (index == 6) {
                        if (cell.getCellType() == CellType.NUMERIC)
                            excelReader.setPayCycleEndDate(String.valueOf(cell.getLocalDateTimeCellValue()));
                    } else if (index == 7) {
                        if (cell.getCellType() == CellType.STRING)
                            excelReader.setEmployeeName(String.valueOf(cell.getStringCellValue()));
                    } else if (index == 8) {
                        if (cell.getCellType() == CellType.STRING)
                            excelReader.setFileNumber(cell.getStringCellValue());
                    }

                }

                excelReaderRepo.save(excelReader);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<EmployeeDto> getSevenConsecutiveDaysWorkingEmployees() {

        List<EmployeeDto> employeesWithConsecutiveDays = new ArrayList<>();

        List<ExcelReader> fetchedList = this.excelReaderRepo.findAll();

        HashSet<String> nameSet = new HashSet<>();

        for (ExcelReader emp : fetchedList) {
            nameSet.add(emp.getEmployeeName());
        }

        for (String name : nameSet) {
            List<ExcelReader> emloyeeList = this.excelReaderRepo.findByEmployeeName(name);

            List<String> timeList = new ArrayList<>();
            for (ExcelReader reader : emloyeeList) {

                String date = reader.getTime();
                if (date == null) continue;

                String[] ts = date.split("T");

                timeList.add(ts[0]);
            }

            timeList.sort(new DateComparator());

            for (int i = 0; i < timeList.size() - 6; i++) {
                if (areConsecutiveDates(timeList.subList(i, i + 7))) {
                    employeesWithConsecutiveDays.add(new EmployeeDto(name, emloyeeList.get(0).getPositionId()));
                    break;
                }
            }
        }

        return employeesWithConsecutiveDays;
    }

    @Override
    public List<EmployeeDto> getEmployeesByShiftTimeGap() {

        List<ExcelReader> fetchedList = this.excelReaderRepo.findAll();

        HashSet<String> nameSet = new HashSet<>();

        for (ExcelReader emp : fetchedList) {
            nameSet.add(emp.getEmployeeName());
        }

        List<EmployeeDto> employeesWithGaps = new ArrayList<>();

        for (String name : nameSet) {

            List<ExcelReader> singelEmloyeeList = this.excelReaderRepo.findByEmployeeName(name);

            HashSet<String> dateSet = new HashSet<>();
            for (ExcelReader emp : singelEmloyeeList) {
                String time = emp.getTime();
                if (time == null) continue;
                String[] ts = time.split("T");
                dateSet.add(ts[0]);
            }

            for (String date : dateSet) {
                List<ExcelReader> shiftSlotList = new ArrayList<>();

                for (ExcelReader employee : singelEmloyeeList) {
                    String time = employee.getTime();
                    String[] ts = time.split("T");
                    if (ts[0].equals(date)) {
                        shiftSlotList.add(employee);
                    }
                }

                for (int i = 0; i < shiftSlotList.size() - 1; i++) {
                    ExcelReader shift1 = shiftSlotList.get(i);
                    ExcelReader shift2 = shiftSlotList.get(i + 1);
//                    ExcelReader shift3 = shiftSlotList.get(i + 2);

                    Duration durationBetweenShifts1 = Duration.between(getTemporal(shift1.getTimeOut()), getTemporal(shift2.getTime()));
//                    Duration durationBetweenShifts2 = Duration.between(getTemporal(shift2.getTimeOut()), getTemporal(shift3.getTime()));

                    // Check if durations meet the specified criteria
                    if (durationBetweenShifts1.toHours() > 1 && durationBetweenShifts1.toHours() < 10) {
                        employeesWithGaps.add(new EmployeeDto(name, singelEmloyeeList.get(0).getPositionId()));
                        break;
                    }
                }
            }


        }


        return employeesWithGaps;
    }

    @Override
    public List<EmployeeDto> getEmployeeWorkedFourteenHrInSingleShift() {

        List<ExcelReader> fetchedList = this.excelReaderRepo.findRecordsWithTimeCardHoursGreaterThanOrEqualFourteen();

        ArrayList<EmployeeDto> employeeDtoArrayList = new ArrayList<>();

        if (fetchedList.size() == 0) {
            return null;
        } else {
            for (ExcelReader employee : fetchedList) {
                employeeDtoArrayList.add(new EmployeeDto(employee.getEmployeeName(), employee.getPositionId()));
            }
        }

        return employeeDtoArrayList;
    }

    public static boolean areConsecutiveDates(List<String> dateStrings) {
        if (dateStrings.size() < 2) {
            return false;
        }

        for (int i = 0; i < dateStrings.size() - 1; i++) {
            LocalDate currentDate = LocalDate.parse(dateStrings.get(i));
            LocalDate nextDate = LocalDate.parse(dateStrings.get(i + 1));

            if (!currentDate.plusDays(1).equals(nextDate)) {
                return false;
            }
        }
        return true;
    }

    static class DateComparator implements Comparator<String> {

        @Override
        public int compare(String date1, String date2) {
            return date1.compareTo(date2);
        }
    }

    static LocalTime getTemporal(String time) {

        String[] ts = time.split("T");

        String[] split = ts[1].split(":");
        return parseToLocalTime(split[0] + ":" + split[1]);
    }

    public static LocalTime parseToLocalTime(String timeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        // Parse the string time using the formatter
        return LocalTime.parse(timeString, formatter);
    }

}
