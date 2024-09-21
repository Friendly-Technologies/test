package com.friendly.commons.models.settings.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecurityUSPDetailsRequest {
    private Integer id;
    private String identifier;
    private IdentifierType identifierType;
    private String description;
    private Boolean active;
    private Integer domainId;
    private List<SecurityUspMtpRequest> securityUspMtpList;

}
