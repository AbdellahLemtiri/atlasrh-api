package com.atlasrh.dto.response;

import com.atlasrh.entity.enums.ContractType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ContractResponseDTO {
    private Long id;
    private ContractType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal grossSalary;
    private Boolean isActive;

    private Long employeeId;
    private String employeeFullName;
}