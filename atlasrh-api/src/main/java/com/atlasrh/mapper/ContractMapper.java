package com.atlasrh.mapper;

import com.atlasrh.dto.request.ContractRequestDTO;
import com.atlasrh.dto.response.ContractResponseDTO;
import com.atlasrh.entity.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContractMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    Contract toEntity(ContractRequestDTO dto);

    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(expression = "java(contract.getEmployee().getFirstName() + \" \" + contract.getEmployee().getLastName())", target = "employeeFullName")
    ContractResponseDTO toResponseDTO(Contract contract);

    List<ContractResponseDTO> toResponseDTOList(List<Contract> contracts);
}