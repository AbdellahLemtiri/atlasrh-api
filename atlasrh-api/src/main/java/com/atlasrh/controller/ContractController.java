package com.atlasrh.controller;

import com.atlasrh.dto.request.ContractRequestDTO;
import com.atlasrh.dto.response.ContractResponseDTO;
import com.atlasrh.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Tag(name = "Contrats", description = "Gestion des contrats par le Responsable RH")
public class ContractController {

    private final ContractService contractService;

    @PostMapping
    @Operation(summary = "Créer un nouveau contrat pour un employé")
    public ResponseEntity<ContractResponseDTO> create(@Valid @RequestBody ContractRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contractService.createContract(dto));
    }

    @PatchMapping("/{id}/terminate")
    @Operation(summary = "Terminer un contrat en cours")
    public ResponseEntity<ContractResponseDTO> terminate(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.terminateContract(id));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Consulter l'historique des contrats d'un employé")
    public ResponseEntity<List<ContractResponseDTO>> getByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(contractService.getContractsByEmployee(employeeId));
    }
}