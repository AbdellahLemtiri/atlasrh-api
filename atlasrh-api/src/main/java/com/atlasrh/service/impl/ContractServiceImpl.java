package com.atlasrh.service.impl;

import com.atlasrh.dto.request.ContractRequestDTO;
import com.atlasrh.dto.response.ContractResponseDTO;
import com.atlasrh.entity.Contract;
import com.atlasrh.entity.Employee;
import com.atlasrh.entity.enums.ContractType;
import com.atlasrh.exception.BusinessException;
import com.atlasrh.exception.ResourceNotFoundException;
import com.atlasrh.mapper.ContractMapper;
import com.atlasrh.repository.ContractRepository;
import com.atlasrh.repository.EmployeeRepository;
import com.atlasrh.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final ContractMapper contractMapper;

    @Override
    public ContractResponseDTO createContract(ContractRequestDTO dto) {
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable avec l'id: " + dto.getEmployeeId()));

        contractRepository.findByEmployeeIdAndIsActiveTrue(dto.getEmployeeId())
                .ifPresent(c -> {
                    throw new BusinessException("L'employé a déjà un contrat actif (ID: " + c.getId() + "). Clôturez-le avant d'en créer un nouveau.");
                });

        if ((dto.getType() == ContractType.CDD || dto.getType() == ContractType.STAGE) && dto.getEndDate() == null) {
            throw new BusinessException("Un contrat de type " + dto.getType() + " doit obligatoirement avoir une date de fin");
        }

        if (dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("La date de fin ne peut pas être antérieure à la date de début");
        }

        Contract contract = contractMapper.toEntity(dto);
        contract.setEmployee(employee);
        contract.setIsActive(true);

        Contract saved = contractRepository.save(contract);
        return contractMapper.toResponseDTO(saved);
    }

    @Override
    public ContractResponseDTO terminateContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat introuvable avec l'id: " + contractId));

        if (!Boolean.TRUE.equals(contract.getIsActive())) {
            throw new BusinessException("Ce contrat est déjà clôturé");
        }

        contract.setIsActive(false);
        if (contract.getEndDate() == null || contract.getEndDate().isAfter(LocalDate.now())) {
            contract.setEndDate(LocalDate.now());
        }

        Contract updated = contractRepository.save(contract);
        return contractMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractResponseDTO> getContractsByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employé introuvable avec l'id: " + employeeId);
        }
        List<Contract> contracts = contractRepository.findByEmployeeIdOrderByStartDateDesc(employeeId);
        return contractMapper.toResponseDTOList(contracts);
    }
}