package com.atlasrh.mapper;

import com.atlasrh.dto.response.LeaveRequestResponseDTO;
import com.atlasrh.entity.LeaveRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LeaveRequestMapper {

    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(expression = "java(leaveRequest.getEmployee().getFirstName() + \" \" + leaveRequest.getEmployee().getLastName())", target = "employeeFullName")
    @Mapping(source = "employee.matricule", target = "employeeMatricule")
    @Mapping(source = "leaveType.id", target = "leaveTypeId")
    @Mapping(source = "leaveType.name", target = "leaveTypeName")
    LeaveRequestResponseDTO toResponseDTO(LeaveRequest leaveRequest);

    List<LeaveRequestResponseDTO> toResponseDTOList(List<LeaveRequest> leaveRequests);
}