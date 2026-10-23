package com.atlasrh.service.impl;

import com.atlasrh.dto.request.EmployeeRequestDTO;
import com.atlasrh.dto.response.EmployeeResponseDTO;
import com.atlasrh.entity.Department;
import com.atlasrh.entity.Employee;
import com.atlasrh.exception.BusinessException;
import com.atlasrh.exception.ResourceNotFoundException;
import com.atlasrh.mapper.EmployeeMapper;
import com.atlasrh.repository.DepartmentRepository;
import com.atlasrh.repository.EmployeeRepository;
import com.atlasrh.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto) {
        if (employeeRepository.existsByMatricule(dto.getMatricule())) {
            throw new BusinessException("Ce matricule est déjà utilisé par un autre employé");
        }
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Cet email est déjà utilisé");
        }

        Department department = departmentRepository.findById(dto.getDepartmentId()).orElseThrow(() -> new ResourceNotFoundException("Département introuvable avec l'id: " + dto.getDepartmentId()));

        Employee employee = employeeMapper.toEntity(dto);
        employee.setDepartment(department);

        if (dto.getManagerId() != null) {
            Employee manager = employeeRepository.findById(dto.getManagerId()).orElseThrow(() -> new ResourceNotFoundException("Manager introuvable avec l'id: " + dto.getManagerId()));
            employee.setManager(manager);
        }

        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toResponseDTO(saved);
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employé introuvable avec l'id: " + id));

        if (employeeRepository.existsByMatriculeAndIdNot(dto.getMatricule(), id)) {
            throw new BusinessException("Ce matricule est déjà utilisé");
        }
        if (employeeRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new BusinessException("Cet email est déjà utilisé");
        }

        if (dto.getManagerId() != null && dto.getManagerId().equals(id)) {
            throw new BusinessException("Un employé ne peut pas être son propre manager");
        }

        Department department = departmentRepository.findById(dto.getDepartmentId()).orElseThrow(() -> new ResourceNotFoundException("Département introuvable avec l'id: " + dto.getDepartmentId()));

        employeeMapper.updateEntityFromDTO(dto, employee);
        employee.setDepartment(department);

        if (dto.getManagerId() != null) {
            Employee manager = employeeRepository.findById(dto.getManagerId()).orElseThrow(() -> new ResourceNotFoundException("Manager introuvable avec l'id: " + dto.getManagerId()));
            employee.setManager(manager);
        } else {
            employee.setManager(null);
        }

        Employee updated = employeeRepository.save(employee);
        return employeeMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employé introuvable avec l'id: " + id));
        return employeeMapper.toResponseDTO(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponseDTO> getAllEmployees(String search, Long departmentId, Pageable pageable) {
        Page<Employee> page;

        if (departmentId != null) {
            page = employeeRepository.findByDepartmentId(departmentId, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            page = employeeRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(search, search, pageable);
        } else {
            page = employeeRepository.findAll(pageable);
        }

        return page.map(employeeMapper::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getTeamByManagerId(Long managerId) {
        if (!employeeRepository.existsById(managerId)) {
            throw new ResourceNotFoundException("Manager introuvable avec l'id: " + managerId);
        }
        List<Employee> team = employeeRepository.findByManagerId(managerId);
        return employeeMapper.toResponseDTOList(team);
    }
}