package com.BlueJayDelivery.bluejaydeliveryBE.service;

import com.BlueJayDelivery.bluejaydeliveryBE.payload.EmployeeDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExcelReaderService {

    void readExcelFile(MultipartFile file);

    List<EmployeeDto> getSevenConsecutiveDaysWorkingEmployees();

    List<EmployeeDto> getEmployeesByShiftTimeGap();

    List<EmployeeDto>getEmployeeWorkedFourteenHrInSingleShift();
}
