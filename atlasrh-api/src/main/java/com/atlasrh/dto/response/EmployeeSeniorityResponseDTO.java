package com.atlasrh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeSeniorityResponseDTO {
    private Long employeeId;
    private String employeeFullName;
    private LocalDate hireDate;
    private int years;
    private int months;
}