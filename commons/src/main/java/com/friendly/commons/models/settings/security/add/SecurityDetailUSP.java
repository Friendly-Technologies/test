package com.friendly.commons.models.settings.security.add;

import com.friendly.commons.models.settings.security.UnderlyingProtocolType;
import com.friendly.commons.models.settings.security.auth.AbstractAuthSecurity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SecurityDetailUSP implements Serializable {

    private Integer id;
    private UnderlyingProtocolType underlyingProtocol;
    private AbstractAuthSecurity auth;

}
