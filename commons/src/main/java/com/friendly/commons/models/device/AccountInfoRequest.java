package com.friendly.commons.models.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoRequest {
    private Integer domainId;
    private String userLogin;
    private String phone;
}