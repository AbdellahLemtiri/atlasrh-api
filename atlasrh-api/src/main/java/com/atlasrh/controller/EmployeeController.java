package com.atlasrh.controller;

import com.atlasrh.dto.request.EmployeeRequestDTO;
import com.atlasrh.dto.response.EmployeeResponseDTO;
import com.atlasrh.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employés", description = "Gestion des fiches employés par le Responsable RH")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @Operation(summary = "Créer la fiche d'un nouvel employé")
    public ResponseEntity<EmployeeResponseDTO> create(@Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier la fiche d'un employé")
    public ResponseEntity<EmployeeResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter la fiche détaillée d'un employé")
    public ResponseEntity<EmployeeResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping
    @Operation(summary = "Lister les employés avec pagination, tri et recherche")
    public ResponseEntity<Page<EmployeeResponseDTO>> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId,
            @PageableDefault(page = 0, size = 10, sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(employeeService.getAllEmployees(search, departmentId, pageable));
    }

    @GetMapping("/manager/{managerId}/team")
    @Operation(summary = "Consulter l'équipe d'un manager")
    public ResponseEntity<List<EmployeeResponseDTO>> getTeam(@PathVariable Long managerId) {
        return ResponseEntity.ok(employeeService.getTeamByManagerId(managerId));
    }
}