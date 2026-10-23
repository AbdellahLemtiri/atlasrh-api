package com.atlasrh.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeResponseDTO {
    private Long id;
    private String matricule;
    private String firstName;
    private String lastName;
    private String email;
    private String position;
    private LocalDate hireDate;

    private Long departmentId;
    private String departmentName;

    private Long managerId;
    private String managerFullName;
}