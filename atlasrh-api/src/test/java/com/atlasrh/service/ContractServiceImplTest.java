package com.atlasrh.service;

import com.atlasrh.dto.request.ContractRequestDTO;
import com.atlasrh.dto.response.ContractResponseDTO;
import com.atlasrh.entity.Contract;
import com.atlasrh.entity.Employee;
import com.atlasrh.entity.enums.ContractType;
import com.atlasrh.exception.BusinessException;
import com.atlasrh.mapper.ContractMapper;
import com.atlasrh.repository.ContractRepository;
import com.atlasrh.repository.EmployeeRepository;
import com.atlasrh.service.impl.ContractServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractServiceImplTest {

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ContractMapper contractMapper;

    @InjectMocks
    private ContractServiceImpl contractService;

    private Employee employee;
    private ContractRequestDTO requestDTO;
    private Contract contract;
    private ContractResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        employee = Employee.builder().id(1L).matricule("EMP001").firstName("Karim").lastName("Tazi").build();

        requestDTO = new ContractRequestDTO();
        requestDTO.setEmployeeId(1L);
        requestDTO.setType(ContractType.CDI);
        requestDTO.setStartDate(LocalDate.of(2024, 1, 1));
        requestDTO.setGrossSalary(new BigDecimal("12000.00"));

        contract = Contract.builder().id(100L).type(ContractType.CDI).startDate(LocalDate.of(2024, 1, 1)).grossSalary(new BigDecimal("12000.00")).isActive(true).employee(employee).build();

        responseDTO = new ContractResponseDTO();
        responseDTO.setId(100L);
        responseDTO.setType(ContractType.CDI);
        responseDTO.setIsActive(true);
    }


    @Test
    @DisplayName("Devrait créer un contrat avec succès quand l'employé n'a aucun contrat actif")
    void shouldCreateContractSuccessfully() {

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        when(contractRepository.findByEmployeeIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());
        when(contractMapper.toEntity(requestDTO)).thenReturn(contract);
        when(contractRepository.save(contract)).thenReturn(contract);
        when(contractMapper.toResponseDTO(contract)).thenReturn(responseDTO);


        ContractResponseDTO result = contractService.createContract(requestDTO);


        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getIsActive()).isTrue();
        verify(contractRepository, times(1)).save(contract);
    }

    @Test
    @DisplayName("Devrait rejeter la création si l'employé a déjà un contrat actif en cours")
    void shouldThrowBusinessExceptionWhenActiveContractAlreadyExists() {
        Contract existingActiveContract = Contract.builder().id(99L).isActive(true).build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(contractRepository.findByEmployeeIdAndIsActiveTrue(1L)).thenReturn(Optional.of(existingActiveContract));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            contractService.createContract(requestDTO);
        });

        assertThat(exception.getMessage()).contains("a déjà un contrat actif");
        verify(contractRepository, never()).save(any(Contract.class));
    }


    @Test
    @DisplayName("Devrait refuser un contrat CDD si la date de fin est absente")
    void shouldThrowBusinessExceptionWhenCddHasNoEndDate() {
        requestDTO.setType(ContractType.CDD);
        requestDTO.setEndDate(null);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(contractRepository.findByEmployeeIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            contractService.createContract(requestDTO);
        });

        assertThat(exception.getMessage()).contains("doit obligatoirement avoir une date de fin");
        verify(contractRepository, never()).save(any(Contract.class));
    }


    @Test
    @DisplayName("Devrait clôturer un contrat actif avec succès")
    void shouldTerminateContractSuccessfully() {
        Long contractId = 100L;
        when(contractRepository.findById(contractId)).thenReturn(Optional.of(contract));
        when(contractRepository.save(contract)).thenReturn(contract);
        when(contractMapper.toResponseDTO(contract)).thenReturn(responseDTO);

        ContractResponseDTO result = contractService.terminateContract(contractId);

        assertThat(contract.getIsActive()).isFalse();
        assertThat(contract.getEndDate()).isNotNull();
        verify(contractRepository, times(1)).save(contract);
    }

    @Test
    @DisplayName("Devrait refuser de clôturer un contrat déjà inactif")
    void shouldThrowBusinessExceptionWhenTerminatingAlreadyInactiveContract() {
        contract.setIsActive(false);
        when(contractRepository.findById(100L)).thenReturn(Optional.of(contract));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            contractService.terminateContract(100L);
        });

        assertThat(exception.getMessage()).contains("Ce contrat est déjà clôturé");
        verify(contractRepository, never()).save(any(Contract.class));
    }
}