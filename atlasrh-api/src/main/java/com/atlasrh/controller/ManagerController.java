package com.atlasrh.controller;

import com.atlasrh.dto.request.LeaveDecisionRequestDTO;
import com.atlasrh.dto.response.LeaveRequestResponseDTO;

import com.atlasrh.service.ManagerService;
import com.atlasrh.service.impl.ManagerServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@Tag(name = "Manager", description = "Validation et gestion des demandes de congé de l'équipe")
public class ManagerController {

    private final ManagerServiceImpl managerService;

    @GetMapping("/{managerId}/pending-requests")
    public ResponseEntity<List<LeaveRequestResponseDTO>> getPendingRequests(@PathVariable Long managerId) {
        return ResponseEntity.ok(managerService.getPendingRequestsByManager(managerId));
    }

    @PatchMapping("/leave-requests/{requestId}/decision")
    public ResponseEntity<LeaveRequestResponseDTO> makeDecision(
            @PathVariable Long requestId,
            @Valid @RequestBody LeaveDecisionRequestDTO dto
    ) {
        return ResponseEntity.ok(managerService.processLeaveDecision(requestId, dto));
    }
}