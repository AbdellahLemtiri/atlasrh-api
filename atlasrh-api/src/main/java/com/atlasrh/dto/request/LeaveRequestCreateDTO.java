package com.atlasrh.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveRequestCreateDTO {

    @NotNull(message = "Le type de congé est obligatoire")
    private Long leaveTypeId;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startDate;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate endDate;

    @NotBlank(message = "Le motif est obligatoire")
    @Size(max = 255, message = "Le motif ne doit pas dépasser 255 caractères")
    private String reason;
}