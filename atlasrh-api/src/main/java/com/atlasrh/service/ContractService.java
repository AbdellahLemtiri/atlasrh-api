package com.atlasrh.service;

import com.atlasrh.dto.request.ContractRequestDTO;
import com.atlasrh.dto.response.ContractResponseDTO;

import java.util.List;

public interface ContractService {
    ContractResponseDTO createContract(ContractRequestDTO dto);
    ContractResponseDTO terminateContract(Long contractId);
    List<ContractResponseDTO> getContractsByEmployee(Long employeeId);
}
