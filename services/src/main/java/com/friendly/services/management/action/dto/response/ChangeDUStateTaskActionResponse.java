package com.friendly.services.management.action.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDUStateTaskActionResponse {
    protected List<InstallOpResponse> installOperations;
    protected List<UpdateOpResponse> updateOperations;
    protected List<UninstallOpResponse> unInstallOperations;
}
