package com.atlasrh.dto.request;

import com.atlasrh.entity.enums.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaveDecisionRequestDTO {

    @NotNull(message = "L'identifiant du manager est obligatoire")
    private Long managerId;

    @NotNull(message = "Le statut de décision est obligatoire (APPROUVEE ou REFUSEE)")
    private LeaveStatus status;

    private String comment;
}