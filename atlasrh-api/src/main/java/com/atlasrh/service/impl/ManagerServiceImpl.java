package com.atlasrh.service.impl;

import com.atlasrh.dto.request.LeaveDecisionRequestDTO;
import com.atlasrh.dto.response.LeaveRequestResponseDTO;
import com.atlasrh.entity.LeaveRequest;
import com.atlasrh.entity.enums.LeaveStatus;
import com.atlasrh.exception.BusinessException;
import com.atlasrh.exception.ResourceNotFoundException;
import com.atlasrh.mapper.LeaveRequestMapper;
import com.atlasrh.repository.EmployeeRepository;
import com.atlasrh.repository.LeaveRequestRepository;
import com.atlasrh.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ManagerServiceImpl  {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveRequestMapper leaveRequestMapper;



    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDTO> getPendingRequestsByManager(Long managerId) {
        if (!employeeRepository.existsById(managerId)) {
            throw new ResourceNotFoundException("Manager introuvable avec l'id : " + managerId);
        }
        List<LeaveRequest> pendingRequests = leaveRequestRepository.findByEmployeeManagerIdAndStatus(
                managerId,
                LeaveStatus.EN_ATTENTE
        );
        return leaveRequestMapper.toResponseDTOList(pendingRequests);
    }


    public LeaveRequestResponseDTO processLeaveDecision(Long requestId, LeaveDecisionRequestDTO dto) {
        LeaveRequest request = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de congé introuvable avec l'id : " + requestId));

        if (request.getStatus() != LeaveStatus.EN_ATTENTE) {
            throw new BusinessException("Impossible de traiter une demande qui n'est pas au statut EN_ATTENTE");
        }

        if (request.getEmployee().getManager() == null ||
                !request.getEmployee().getManager().getId().equals(dto.getManagerId())) {
            throw new BusinessException("Vous n'êtes pas autorisé à valider les demandes de cet employé");
        }

        if (dto.getStatus() == LeaveStatus.REFUSEE) {
            if (dto.getComment() == null || dto.getComment().trim().isEmpty()) {
                throw new BusinessException("Un motif de refus (commentaire) est obligatoire");
            }
            request.setStatus(LeaveStatus.REFUSEE);
            request.setRejectionComment(dto.getComment().trim());
        }
        else if (dto.getStatus() == LeaveStatus.APPROUVEE) {
            int currentYear = request.getStartDate().getYear();
            int totalAllowed = request.getLeaveType().getDefaultDaysPerYear();
            int alreadyUsed = leaveRequestRepository.countApprovedDaysByYearAndType(
                    request.getEmployee().getId(),
                    request.getLeaveType().getId(),
                    currentYear
            );
            int remainingBalance = totalAllowed - alreadyUsed;

            if (request.getBusinessDaysCount() > remainingBalance) {
                throw new BusinessException("Solde insuffisant : il reste " + remainingBalance +
                        " jour(s), mais la demande est de " + request.getBusinessDaysCount() + " jour(s)");
            }

            request.setStatus(LeaveStatus.APPROUVEE);
            request.setRejectionComment(dto.getComment() != null ? dto.getComment().trim() : null);
        } else {
            throw new BusinessException("Statut invalide. Utilisez APPROUVEE ou REFUSEE");
        }

        LeaveRequest updated = leaveRequestRepository.save(request);
        return leaveRequestMapper.toResponseDTO(updated);
    }
}