package com.atlasrh.repository;

import com.atlasrh.entity.LeaveRequest;
import com.atlasrh.entity.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeManagerIdAndStatus(Long managerId, LeaveStatus status);

    @Query("SELECT COALESCE(SUM(lr.businessDaysCount), 0) FROM LeaveRequest lr " +
            "WHERE lr.employee.id = :employeeId " +
            "AND lr.leaveType.id = :leaveTypeId " +
            "AND lr.status = 'APPROUVEE' " +
            "AND YEAR(lr.startDate) = :year")
    int countApprovedDaysByYearAndType(
            @Param("employeeId") Long employeeId,
            @Param("leaveTypeId") Long leaveTypeId,
            @Param("year") int year
    );


}