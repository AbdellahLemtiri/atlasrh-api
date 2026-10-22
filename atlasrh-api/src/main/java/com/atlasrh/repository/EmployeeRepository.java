package com.atlasrh.repository;

import com.atlasrh.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByMatricule(String matricule);
    boolean existsByEmail(String email);
    
    boolean existsByMatriculeAndIdNot(String matricule, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);

    List<Employee> findByManagerId(Long managerId);

    Page<Employee> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(String lastName, String firstName, Pageable pageable);
    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);
}