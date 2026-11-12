package com.atlasrh.service;


import com.atlasrh.dto.request.EmployeeRequestDTO;
import com.atlasrh.dto.response.EmployeeResponseDTO;
import com.atlasrh.entity.Department;
import com.atlasrh.entity.Employee;
import com.atlasrh.exception.BusinessException;
import com.atlasrh.exception.ResourceNotFoundException;
import com.atlasrh.mapper.EmployeeMapper;
import com.atlasrh.repository.DepartmentRepository;
import com.atlasrh.repository.EmployeeRepository;
import com.atlasrh.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeRequestDTO requestDTO;
    private Department department;
    private Employee employee;
    private EmployeeResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        department = Department.builder()
                .id(1L)
                .name("Informatique")
                .build();

        requestDTO = new EmployeeRequestDTO();
        requestDTO.setMatricule("EMP001");
        requestDTO.setFirstName("Ahmed");
        requestDTO.setLastName("Alami");
        requestDTO.setEmail("ahmed.alami@atlasrh.com");
        requestDTO.setPosition("Développeur Java");
        requestDTO.setHireDate(LocalDate.of(2023, 1, 15));
        requestDTO.setDepartmentId(1L);

        employee = Employee.builder()
                .id(10L)
                .matricule("EMP001")
                .firstName("Ahmed")
                .lastName("Alami")
                .email("ahmed.alami@atlasrh.com")
                .position("Développeur Java")
                .hireDate(LocalDate.of(2023, 1, 15))
                .department(department)
                .build();

        responseDTO = new EmployeeResponseDTO();
        responseDTO.setId(10L);
        responseDTO.setMatricule("EMP001");
        responseDTO.setDepartmentId(1L);
    }

    @Test
    public void shouldCreateEmplyeeSuccessfully() {

        when(employeeRepository.existsByMatricule("EMP001")).thenReturn(false);
        when(employeeRepository.existsByEmail("ahmed.alami@atlasrh.com")).thenReturn(true);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeMapper.toEntity(requestDTO)).thenReturn(employee);
        when(employeeMapper.toResponseDTO(employee)).thenReturn(responseDTO);


        EmployeeResponseDTO result = employeeService.createEmployee(requestDTO);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getMatricule()).isEqualTo("EMP001");

        verify(employeeRepository, times(1)).save(employee);


    }

    @Test
    @DisplayName("Devrait lever BusinessException si le matricule existe déjà")
    void shouldThrowBusinessExceptionWhenMatriculeAlreadyExists() {
        when(employeeRepository.existsByMatricule("EMP001")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            employeeService.createEmployee(requestDTO);
        });

        assertThat(exception.getMessage()).contains("Ce matricule est déjà utilisé");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Devrait lever BusinessException si l'email existe déjà")
    void shouldThrowBusinessExceptionWhenEmailAlreadyExists() {
        when(employeeRepository.existsByMatricule("EMP001")).thenReturn(false);
        when(employeeRepository.existsByEmail("ahmed.alami@atlasrh.com")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            employeeService.createEmployee(requestDTO);
        });

        assertThat(exception.getMessage()).contains("Cet email est déjà utilisé");
        verify(employeeRepository, never()).save(any(Employee.class));
    }


    @Test
    @DisplayName("Devrait refuser qu'un employé soit son propre manager lors d'une modification")
    void shouldRejectEmployeeAsOwnManager() {
        Long employeeId = 10L;
        requestDTO.setManagerId(10L);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByMatriculeAndIdNot("EMP001", employeeId)).thenReturn(false);
        when(employeeRepository.existsByEmailAndIdNot("ahmed.alami@atlasrh.com", employeeId)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            employeeService.updateEmployee(employeeId, requestDTO);
        });

        assertThat(exception.getMessage()).contains("Un employé ne peut pas être son propre manager");
        verify(employeeRepository, never()).save(any(Employee.class));
    }



    @Test
    @DisplayName("Devrait lister l'équipe d'un manager existant")
    void shouldReturnTeamWhenManagerExists() {
        Long managerId = 5L;
        when(employeeRepository.existsById(managerId)).thenReturn(true);
        when(employeeRepository.findByManagerId(managerId)).thenReturn(List.of(employee));
        when(employeeMapper.toResponseDTOList(any())).thenReturn(List.of(responseDTO));

        List<EmployeeResponseDTO> team = employeeService.getTeamByManagerId(managerId);

        assertThat(team).isNotEmpty().hasSize(1);
        verify(employeeRepository, times(1)).findByManagerId(managerId);
    }

    @Test
    @DisplayName("Devrait lever ResourceNotFoundException si le manager n'existe pas")
    void shouldThrowNotFoundWhenManagerDoesNotExist() {
        Long invalidManagerId = 999L;
        when(employeeRepository.existsById(invalidManagerId)).thenReturn(false);


        assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.getTeamByManagerId(invalidManagerId);
        });

        verify(employeeRepository, never()).findByManagerId(anyLong());
    }



}
