package com.BlueJayDelivery.bluejaydeliveryBE.repo;

import com.BlueJayDelivery.bluejaydeliveryBE.entity.ExcelReader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExcelReaderRepo extends JpaRepository<ExcelReader, Integer> {

    List<ExcelReader> findByEmployeeName(String empName);

    @Query(value = "SELECT * FROM excel_reader WHERE time_card_hours <> '' AND CAST(time_card_hours AS TIME) >= '14:00'", nativeQuery = true)
    List<ExcelReader> findRecordsWithTimeCardHoursGreaterThanOrEqualFourteen();
}

