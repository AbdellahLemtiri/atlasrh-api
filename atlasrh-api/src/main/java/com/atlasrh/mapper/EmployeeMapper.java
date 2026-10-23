package com.atlasrh.mapper;

import com.atlasrh.dto.request.EmployeeRequestDTO;
import com.atlasrh.dto.response.EmployeeResponseDTO;
import com.atlasrh.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "subordinates", ignore = true)
    @Mapping(target = "contracts", ignore = true)
    @Mapping(target = "leaveRequests", ignore = true)
    Employee toEntity(EmployeeRequestDTO dto);

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")
    @Mapping(source = "manager.id", target = "managerId")
    @Mapping(expression = "java(employee.getManager() != null ? employee.getManager().getFirstName() + \" \" + employee.getManager().getLastName() : null)", target = "managerFullName")
    EmployeeResponseDTO toResponseDTO(Employee employee);

    List<EmployeeResponseDTO> toResponseDTOList(List<Employee> employees);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "manager", ignore = true)
    void updateEntityFromDTO(EmployeeRequestDTO dto, @MappingTarget Employee entity);
}