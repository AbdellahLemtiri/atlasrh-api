package com.atlasrh.repository;

import com.atlasrh.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByEmployeeIdOrderByStartDateDesc(Long employeeId);

    Optional<Contract> findByEmployeeIdAndIsActiveTrue(Long employeeId);
}