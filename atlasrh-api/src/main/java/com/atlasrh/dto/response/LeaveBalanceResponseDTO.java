package com.atlasrh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveBalanceResponseDTO {
    private Long leaveTypeId;
    private String leaveTypeName;
    private Integer defaultDaysPerYear;
    private Integer approvedDays;
    private Integer remainingBalance;
}