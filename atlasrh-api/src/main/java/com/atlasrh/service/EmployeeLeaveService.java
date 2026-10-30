package com.atlasrh.service;

import com.atlasrh.dto.request.LeaveRequestCreateDTO;
import com.atlasrh.dto.response.EmployeeSeniorityResponseDTO;
import com.atlasrh.dto.response.LeaveBalanceResponseDTO;
import com.atlasrh.dto.response.LeaveRequestResponseDTO;
import com.atlasrh.entity.enums.LeaveStatus;

import java.util.List;

public interface EmployeeLeaveService {
    LeaveRequestResponseDTO createLeaveRequest(Long employeeId, LeaveRequestCreateDTO dto);
    List<LeaveRequestResponseDTO> getMyLeaveRequests(Long employeeId, LeaveStatus status);
    LeaveRequestResponseDTO cancelLeaveRequest(Long employeeId, Long requestId);
    List<LeaveBalanceResponseDTO> getMyBalances(Long employeeId);
    EmployeeSeniorityResponseDTO getMySeniority(Long employeeId);
}