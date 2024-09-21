package com.friendly.commons.models.settings.security.add;

import static com.friendly.commons.models.settings.security.ProtocolSecurityType.LWM2M;
import com.friendly.commons.models.settings.security.MaskType;
import com.friendly.commons.models.settings.security.ServerType;
import com.friendly.commons.models.settings.security.auth.AbstractAuthSecurity;
import com.friendly.commons.models.settings.security.oscore.Oscore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddSecurityLWM2M extends AddAbstractSecurity {

    private MaskType maskType;
    private ServerType serverType;
    private AbstractAuthSecurity auth;
    private Oscore oscore;

    @Builder
    public AddSecurityLWM2M(final Integer id,
                            final Integer domainId,
                            final String mask,
                            final MaskType maskType,
                            final ServerType serverType,
                            final AbstractAuthSecurity auth,
                            final Oscore oscore) {
        super(id, domainId, LWM2M, mask);

        this.maskType = maskType;
        this.serverType = serverType;
        this.auth = auth;
        this.oscore = oscore;
    }

}
