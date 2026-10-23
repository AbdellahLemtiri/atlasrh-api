package com.atlasrh.service;

import com.atlasrh.dto.request.EmployeeRequestDTO;
import com.atlasrh.dto.response.EmployeeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto);
    EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto);
    EmployeeResponseDTO getEmployeeById(Long id);
    Page<EmployeeResponseDTO> getAllEmployees(String search, Long departmentId, Pageable pageable);
    List<EmployeeResponseDTO> getTeamByManagerId(Long managerId);
}