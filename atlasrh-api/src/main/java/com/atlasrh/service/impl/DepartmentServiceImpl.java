package com.atlasrh.service.impl;

import org.springframework.stereotype.Service;

import com.atlasrh.dto.request.DepartmentRequestDTO;
import com.atlasrh.dto.response.DepartmentResponseDTO;
import com.atlasrh.entity.Department;
import com.atlasrh.exception.BusinessException;
import com.atlasrh.exception.ResourceNotFoundException;
import com.atlasrh.mapper.DepartmentMapper;
import com.atlasrh.repository.DepartmentRepository;
import com.atlasrh.service.DepartmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private DepartmentRepository departmentRepository;
    private DepartmentMapper departmentMapper;

    @Override
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO departmentRequestDTO) {
        if (departmentRepository.existsByNameIgnoreCase(departmentRequestDTO.getName())) {
            throw new BusinessException("Un département avec ce nom existe déjà");
        }

        Department department = departmentMapper.toEntity(departmentRequestDTO);
        Department departmentSaved = departmentRepository.save(department);
        return departmentMapper.toResponseDTO(departmentSaved);
    }

    @Override
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO departmentRequestDTO) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département introuvable"));

        if (departmentRepository.existsByNameIgnoreCaseAndIdNot(departmentRequestDTO.getName(), id)) {
            throw new BusinessException("Un département avec ce nom existe déjà");
        }

        departmentMapper.updateEntityFromDTO(departmentRequestDTO, department);
        Department departmentSaved = departmentRepository.save(department);
        return departmentMapper.toResponseDTO(departmentSaved);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponseDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département introuvable "));
        return departmentMapper.toResponseDTO(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentMapper.toResponseDTOList(departmentRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département introuvable "));
        if (!department.getEmployees().isEmpty()) {
            throw new BusinessException("Impossible de supprimer un département contenant des employés");
        }
        departmentRepository.delete(department);
    }
}
