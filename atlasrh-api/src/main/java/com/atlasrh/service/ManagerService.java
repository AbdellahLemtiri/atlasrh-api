package com.atlasrh.service;

import com.atlasrh.dto.request.LeaveDecisionRequestDTO;
import com.atlasrh.dto.response.LeaveRequestResponseDTO;

import java.util.List;

public interface ManagerService {
    List<LeaveRequestResponseDTO> getPendingRequestByManager(Long managerId);
    LeaveRequestResponseDTO processLeaveDecision(Long requestId , LeaveDecisionRequestDTO leaveDecisionRequestDTO);
}
