package com.atlasrh.controller;

import com.atlasrh.dto.request.DepartmentRequestDTO;
import com.atlasrh.dto.response.DepartmentResponseDTO;
import com.atlasrh.service.DepartmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Départements", description = "Gestion des départements par le Responsable RH")
public class DepartementController {
    private DepartmentService departmentService;


    @PostMapping
    public ResponseEntity<DepartmentResponseDTO> create(@Valid @RequestBody DepartmentRequestDTO departmentRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.createDepartment(departmentRequestDTO)
        );
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> getAll() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> update(@PathVariable Long id, @Valid @RequestBody DepartmentRequestDTO dto) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }


}
