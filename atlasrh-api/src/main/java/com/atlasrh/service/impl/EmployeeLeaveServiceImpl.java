package com.atlasrh.service.impl;

import com.atlasrh.dto.request.LeaveRequestCreateDTO;
import com.atlasrh.dto.response.EmployeeSeniorityResponseDTO;
import com.atlasrh.dto.response.LeaveBalanceResponseDTO;
import com.atlasrh.dto.response.LeaveRequestResponseDTO;
import com.atlasrh.entity.Employee;
import com.atlasrh.entity.LeaveRequest;
import com.atlasrh.entity.LeaveType;
import com.atlasrh.entity.enums.LeaveStatus;
import com.atlasrh.exception.BusinessException;
import com.atlasrh.exception.ResourceNotFoundException;
import com.atlasrh.mapper.LeaveRequestMapper;
import com.atlasrh.repository.EmployeeRepository;
import com.atlasrh.repository.LeaveRequestRepository;
import com.atlasrh.repository.LeaveTypeRepository;
import com.atlasrh.service.EmployeeLeaveService;
import com.atlasrh.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeLeaveServiceImpl implements EmployeeLeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestMapper leaveRequestMapper;

    @Override
    public LeaveRequestResponseDTO createLeaveRequest(Long employeeId, LeaveRequestCreateDTO dto) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new ResourceNotFoundException("Employé introuvable avec l'id : " + employeeId));

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId()).orElseThrow(() -> new ResourceNotFoundException("Type de congé introuvable avec l'id : " + dto.getLeaveTypeId()));

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BusinessException("La date de fin ne peut pas être antérieure à la date de début");
        }

        int businessDays = DateUtils.calculateBusinessDays(dto.getStartDate(), dto.getEndDate());
        if (businessDays == 0) {
            throw new BusinessException("La période sélectionnée ne comporte aucun jour ouvré");
        }

        if (leaveRequestRepository.hasOverlappingRequests(employeeId, dto.getStartDate(), dto.getEndDate())) {
            throw new BusinessException("Vous avez déjà une demande de congé déposée sur cette période");
        }

        int targetYear = dto.getStartDate().getYear();
        int approvedDays = leaveRequestRepository.countApprovedDaysByYearAndType(employeeId, leaveType.getId(), targetYear);
        int remainingBalance = leaveType.getDefaultDaysPerYear() - approvedDays;

        if (businessDays > remainingBalance) {
            throw new BusinessException("Solde insuffisant pour ce type de congé : solde restant = " + remainingBalance + " jour(s), demandé = " + businessDays + " jour(s)");
        }

        LeaveRequest leaveRequest = LeaveRequest.builder().startDate(dto.getStartDate()).endDate(dto.getEndDate()).businessDaysCount(businessDays).reason(dto.getReason()).status(LeaveStatus.EN_ATTENTE).employee(employee).leaveType(leaveType).build();

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);
        return leaveRequestMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveRequestResponseDTO> getMyLeaveRequests(Long employeeId, LeaveStatus status) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employé introuvable avec l'id : " + employeeId);
        }

        List<LeaveRequest> requests;
        if (status != null) {
            requests = leaveRequestRepository.findByEmployeeIdAndStatusOrderByStartDateDesc(employeeId, status);
        } else {
            requests = leaveRequestRepository.findByEmployeeIdOrderByStartDateDesc(employeeId);
        }

        return leaveRequestMapper.toResponseDTOList(requests);
    }

    @Override
    public LeaveRequestResponseDTO cancelLeaveRequest(Long employeeId, Long requestId) {
        LeaveRequest request = leaveRequestRepository.findById(requestId).orElseThrow(() -> new ResourceNotFoundException("Demande de congé introuvable avec l'id : " + requestId));

        if (!request.getEmployee().getId().equals(employeeId)) {
            throw new BusinessException("Vous n'êtes pas autorisé à annuler cette demande");
        }

        if (request.getStatus() != LeaveStatus.EN_ATTENTE) {
            throw new BusinessException("Impossible d'annuler une demande ayant déjà le statut " + request.getStatus());
        }

        request.setStatus(LeaveStatus.ANNULEE);
        LeaveRequest updated = leaveRequestRepository.save(request);
        return leaveRequestMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalanceResponseDTO> getMyBalances(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employé introuvable avec l'id : " + employeeId);
        }

        int currentYear = LocalDate.now().getYear();
        List<LeaveType> types = leaveTypeRepository.findAll();
        List<LeaveBalanceResponseDTO> balances = new ArrayList<>();

        for (LeaveType type : types) {
            int approved = leaveRequestRepository.countApprovedDaysByYearAndType(employeeId, type.getId(), currentYear);
            int remaining = type.getDefaultDaysPerYear() - approved;

            balances.add(LeaveBalanceResponseDTO.builder().leaveTypeId(type.getId()).leaveTypeName(type.getName()).defaultDaysPerYear(type.getDefaultDaysPerYear()).approvedDays(approved).remainingBalance(remaining).build());
        }

        return balances;
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeSeniorityResponseDTO getMySeniority(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new ResourceNotFoundException("Employé introuvable avec l'id : " + employeeId));

        Period period = Period.between(employee.getHireDate(), LocalDate.now());

        return EmployeeSeniorityResponseDTO.builder().employeeId(employee.getId()).employeeFullName(employee.getFirstName() + " " + employee.getLastName()).hireDate(employee.getHireDate()).years(period.getYears()).months(period.getMonths()).build();
    }
}