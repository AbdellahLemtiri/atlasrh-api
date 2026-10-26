package com.atlasrh.dto.response;

import com.atlasrh.entity.enums.LeaveStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LeaveRequestResponseDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer businessDaysCount;
    private String reason;
    private LeaveStatus status;
    private String rejectionComment;

    private Long employeeId;
    private String employeeFullName;
    private String employeeMatricule;

    private Long leaveTypeId;
    private String leaveTypeName;
}