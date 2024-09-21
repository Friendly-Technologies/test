package com.friendly.commons.models.settings.security.add;

import static com.friendly.commons.models.settings.security.ProtocolSecurityType.MQTT;
import com.friendly.commons.models.settings.security.MaskType;
import com.friendly.commons.models.settings.security.auth.AbstractAuthSecurity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddSecurityMQTT extends AddAbstractSecurity {

    private MaskType maskType;
    private AbstractAuthSecurity auth;

    @Builder
    public AddSecurityMQTT(final Integer id,
                           final Integer domainId,
                           final String mask,
                           final MaskType maskType,
                           final AbstractAuthSecurity auth) {
        super(id, domainId, MQTT, mask);

        this.maskType = maskType;
        this.auth = auth;
    }

}
