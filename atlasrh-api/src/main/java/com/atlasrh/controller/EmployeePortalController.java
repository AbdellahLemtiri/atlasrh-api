package com.atlasrh.controller;

import com.atlasrh.dto.request.LeaveRequestCreateDTO;
import com.atlasrh.dto.response.EmployeeSeniorityResponseDTO;
import com.atlasrh.dto.response.LeaveBalanceResponseDTO;
import com.atlasrh.dto.response.LeaveRequestResponseDTO;
import com.atlasrh.entity.enums.LeaveStatus;
import com.atlasrh.service.EmployeeLeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/{employeeId}")
@RequiredArgsConstructor
@Tag(name = "Portail Employé", description = "Demandes de congés, suivi des soldes et ancienneté")
public class EmployeePortalController {

    private final EmployeeLeaveService employeeLeaveService;

    @PostMapping("/leave-requests")
    @Operation(summary = "Créer une demande de congé")
    public ResponseEntity<LeaveRequestResponseDTO> createRequest(
            @PathVariable Long employeeId,
            @Valid @RequestBody LeaveRequestCreateDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeLeaveService.createLeaveRequest(employeeId, dto));
    }

    @GetMapping("/leave-requests")
    @Operation(summary = "Consulter mes demandes avec filtre optionnel par statut")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getMyRequests(
            @PathVariable Long employeeId,
            @RequestParam(required = false) LeaveStatus status
    ) {
        return ResponseEntity.ok(employeeLeaveService.getMyLeaveRequests(employeeId, status));
    }

    @PatchMapping("/leave-requests/{requestId}/cancel")
    @Operation(summary = "Annuler une demande de congé en attente")
    public ResponseEntity<LeaveRequestResponseDTO> cancelRequest(
            @PathVariable Long employeeId,
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(employeeLeaveService.cancelLeaveRequest(employeeId, requestId));
    }

    @GetMapping("/leave-balances")
    @Operation(summary = "Consulter mon solde restant par type de congé")
    public ResponseEntity<List<LeaveBalanceResponseDTO>> getMyBalances(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeLeaveService.getMyBalances(employeeId));
    }

    @GetMapping("/seniority")
    @Operation(summary = "Consulter mon ancienneté en années et mois")
    public ResponseEntity<EmployeeSeniorityResponseDTO> getMySeniority(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeLeaveService.getMySeniority(employeeId));
    }
}