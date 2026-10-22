package com.atlasrh.mapper;

import com.atlasrh.dto.request.DepartmentRequestDTO;
import com.atlasrh.dto.response.DepartmentResponseDTO;
import com.atlasrh.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    Department toEntity(DepartmentRequestDTO dto);
    DepartmentResponseDTO toResponseDTO(Department entity);
    List<DepartmentResponseDTO> toResponseDTOList(List<Department> entities);
    void updateEntityFromDTO(DepartmentRequestDTO dto, @MappingTarget Department entity);
}
            