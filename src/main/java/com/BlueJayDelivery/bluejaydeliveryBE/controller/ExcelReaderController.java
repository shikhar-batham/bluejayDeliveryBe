package com.BlueJayDelivery.bluejaydeliveryBE.controller;

import com.BlueJayDelivery.bluejaydeliveryBE.payload.EmployeeDto;
import com.BlueJayDelivery.bluejaydeliveryBE.service.ExcelReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/excel")
public class ExcelReaderController {

    @Autowired
    private ExcelReaderService excelReaderService;

    @PostMapping("/")
    public void readExcelFile(@RequestParam("file") MultipartFile file) {
        this.excelReaderService.readExcelFile(file);
    }

    @GetMapping("/getEmp")
    public ResponseEntity<List<EmployeeDto>> getSevenConsecutiveDaysWorkingEmployees() {

        List<EmployeeDto> sevenConsecutiveDaysWorkingEmployees = this.excelReaderService.getSevenConsecutiveDaysWorkingEmployees();

        if (sevenConsecutiveDaysWorkingEmployees.size() == 0) {
            System.out.println("There is no employee who have worked for seven consecutive days.");
        } else {
            for (EmployeeDto emp : sevenConsecutiveDaysWorkingEmployees) {
                System.out.println(emp.getName() + " " + emp.getPosition());
            }
        }

        return new ResponseEntity<>(sevenConsecutiveDaysWorkingEmployees, HttpStatus.OK);
    }

    @GetMapping("/getEmployeeWithShiftGap")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByShiftTimeGap() {

        List<EmployeeDto> employeesByShiftTimeGap = this.excelReaderService.getEmployeesByShiftTimeGap();

        if (employeesByShiftTimeGap == null) {
            System.out.println("There is no employees who have shift gap greater than 1 and less than 10");
        } else {
            System.out.println();
            System.out.println("Employee Name, Employee position  who have shift gap greater than 1 and less than 10");
            for (EmployeeDto emp : employeesByShiftTimeGap) {
                System.out.println(emp.getName() + " " + emp.getPosition());
            }
        }

        return new ResponseEntity<>(employeesByShiftTimeGap, HttpStatus.OK);
    }

    @GetMapping("/getEmpSingleShift")
    public ResponseEntity<List<EmployeeDto>> getEmployeeWorkedFourteenHrInSingleShift() {

        List<EmployeeDto> employeeWorkedFourteenHrInSingleShift = this.excelReaderService.getEmployeeWorkedFourteenHrInSingleShift();

        if (employeeWorkedFourteenHrInSingleShift == null) {
            System.out.println("There is no employee who have worked 14 hrs in a single shift");
        } else {
            System.out.println();
            System.out.println("Employee Name, Employee position who have worked 14 hrs in a single shift");
            for (EmployeeDto dto : employeeWorkedFourteenHrInSingleShift) {
                System.out.println(dto.getName() + " " + dto.getPosition());
            }
        }

        return new ResponseEntity<>(employeeWorkedFourteenHrInSingleShift, HttpStatus.OK);
    }
}
