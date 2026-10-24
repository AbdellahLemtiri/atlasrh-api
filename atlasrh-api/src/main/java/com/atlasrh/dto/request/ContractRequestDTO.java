package com.atlasrh.dto.request;

import com.atlasrh.entity.enums.ContractType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ContractRequestDTO {

    @NotNull(message = "L'identifiant de l'employé est obligatoire")
    private Long employeeId;

    @NotNull(message = "Le type de contrat est obligatoire")
    private ContractType type;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "Le salaire brut est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le salaire brut doit être strictement positif")
    private BigDecimal grossSalary;
}