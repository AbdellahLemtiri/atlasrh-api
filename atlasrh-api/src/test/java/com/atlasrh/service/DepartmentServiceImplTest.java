package com.atlasrh.service;

import com.atlasrh.dto.request.DepartmentRequestDTO;
import com.atlasrh.dto.response.DepartmentResponseDTO;
import com.atlasrh.entity.Department;
import com.atlasrh.entity.Employee;
import com.atlasrh.exception.BusinessException;
import com.atlasrh.exception.ResourceNotFoundException;
import com.atlasrh.mapper.DepartmentMapper;
import com.atlasrh.repository.DepartmentRepository;
import com.atlasrh.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;


    @Test
    @DisplayName("Devrait créer un département avec succès lorsque le nom est unique")
    void shouldCreateDepartmentSuccessfully() {
        DepartmentRequestDTO requestDTO = new DepartmentRequestDTO();
        requestDTO.setName("Ressources Humaines");
        requestDTO.setDescription("Gestion du personnel");

        Department entityToSave = Department.builder().name("Ressources Humaines").build();
        Department savedEntity = Department.builder().id(1L).name("Ressources Humaines").build();
        DepartmentResponseDTO expectedResponse = new DepartmentResponseDTO();
        expectedResponse.setId(1L);
        expectedResponse.setName("Ressources Humaines");

        when(departmentRepository.existsByNameIgnoreCase("Ressources Humaines")).thenReturn(false);
        when(departmentMapper.toEntity(requestDTO)).thenReturn(entityToSave);
        when(departmentRepository.save(entityToSave)).thenReturn(savedEntity);
        when(departmentMapper.toResponseDTO(savedEntity)).thenReturn(expectedResponse);

        DepartmentResponseDTO actualResponse = departmentService.createDepartment(requestDTO);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.getId()).isEqualTo(1L);
        assertThat(actualResponse.getName()).isEqualTo("Ressources Humaines");

        verify(departmentRepository, times(1)).existsByNameIgnoreCase("Ressources Humaines");
        verify(departmentRepository, times(1)).save(entityToSave);
    }







    @Test
    @DisplayName("Devrait lever une BusinessException lorsque le nom du département existe déjà")
    void shouldThrowBusinessExceptionWhenDepartmentNameAlreadyExists() {
        DepartmentRequestDTO requestDTO = new DepartmentRequestDTO();
        requestDTO.setName("Informatique");

        when(departmentRepository.existsByNameIgnoreCase("Informatique")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            departmentService.createDepartment(requestDTO);
        });

        assertThat(exception.getMessage()).contains("Un département avec ce nom existe déjà");
        verify(departmentRepository, never()).save(any(Department.class));
    }


    @Test
    @DisplayName("Devrait lever une BusinessException lorsqu'on supprime un département contenant des employés")
    void shouldThrowBusinessExceptionWhenDeletingDepartmentWithEmployees() {
        Long departmentId = 1L;
        Department departmentWithEmployees = Department.builder()
                .id(departmentId)
                .name("IT")
                .employees(List.of(new Employee()))
                .build();

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(departmentWithEmployees));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            departmentService.deleteDepartment(departmentId);
        });

        assertThat(exception.getMessage()).contains("Impossible de supprimer un département contenant des employés");
        verify(departmentRepository, never()).delete(any(Department.class));
    }

    @Test
    @DisplayName("Devrait lever une ResourceNotFoundException lorsqu'on supprime un département inexistant")
    void shouldThrowNotFoundExceptionWhenDepartmentDoesNotExist() {
        Long invalidId = 999L;
        when(departmentRepository.findById(invalidId)).thenReturn(Optional.empty());


        assertThrows(ResourceNotFoundException.class, () -> {
            departmentService.deleteDepartment(invalidId);
        });

        verify(departmentRepository, never()).delete(any(Department.class));
    }
}