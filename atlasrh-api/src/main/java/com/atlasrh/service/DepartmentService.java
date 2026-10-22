package com.atlasrh.service;

import java.util.List;

import com.atlasrh.dto.request.DepartmentRequestDTO;
import com.atlasrh.dto.response.DepartmentResponseDTO;

public interface DepartmentService {
    DepartmentResponseDTO createDepartment(DepartmentRequestDTO departmentRequestDTO);
    DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO departmentRequestDTO);
    DepartmentResponseDTO getDepartmentById(Long id);
    List<DepartmentResponseDTO> getAllDepartments();
    void deleteDepartment(Long id);
}