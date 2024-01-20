package com.BlueJayDelivery.bluejaydeliveryBE.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "excel_reader")
public class ExcelReader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String positionId;
    private String positionStatus;
    private String time;
    private String timeOut;
    private String timeCardHours;
    private String payCycleStartDate;
    private String payCycleEndDate;
    private String employeeName;
    private String fileNumber;
}
