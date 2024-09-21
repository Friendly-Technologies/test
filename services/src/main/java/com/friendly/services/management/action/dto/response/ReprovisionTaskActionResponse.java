package com.friendly.services.management.action.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReprovisionTaskActionResponse extends AbstractActionResponse {
    private boolean sendProfile;
    private boolean sendCPEProvision;
    private boolean sendCPEProvisionAttribute;
    private boolean customRPC;
    private boolean cpeProvisionObject;
    private boolean cpeFile;
}
