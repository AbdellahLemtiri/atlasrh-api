package com.atlasrh.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveRequestRequestDTO {

    @NotBlank(message = "la datte doi étre n'est pas null ")
    private LocalDate startDate;

    @NotBlank(message = "la datte débute doit étre n'est pas null ")
    private LocalDate endDate;

    @NotBlank(message = "le motif est obligatoire !")
    private String reason;

    @NotNull(message = "le id de l'employee est obligatoire ")
    private Long employeeId;

}

