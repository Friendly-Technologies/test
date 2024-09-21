package com.friendly.commons.models.settings.security;

import static com.friendly.commons.models.settings.security.MaskType.EQUALS;
import static com.friendly.commons.models.settings.security.MaskType.LIKE;
import static com.friendly.commons.models.settings.security.auth.SecurityModeType.BASIC;
import static com.friendly.commons.models.settings.security.auth.SecurityModeType.NO_SEC;
import static com.friendly.commons.models.settings.security.auth.SecurityModeType.PSK;
import static com.friendly.commons.models.settings.security.auth.SecurityModeType.X_509;
import static com.friendly.commons.models.settings.security.ServerType.ALL;
import static com.friendly.commons.models.settings.security.ServerType.BS;

import com.friendly.commons.models.settings.security.auth.SecurityModeType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ProtocolSecurityType {

    LWM2M(Arrays.asList(ALL, BS, ServerType.LWM2M), Arrays.asList(PSK, X_509, NO_SEC), Arrays.asList(LIKE, EQUALS)),
    MQTT(Collections.emptyList(), Arrays.asList(BASIC,  NO_SEC), Arrays.asList(LIKE, EQUALS)),
    USP(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

    private final List<ServerType> serverTypes;
    private final List<SecurityModeType> securityModes;
    private final List<MaskType> maskTypes;

    ProtocolSecurityType(final List<ServerType> serverTypes,
                         final List<SecurityModeType> securityModes,
                         final List<MaskType> maskTypes) {
        this.serverTypes = serverTypes;
        this.securityModes = securityModes;
        this.maskTypes = maskTypes;
    }

    public List<ServerType> getServerTypes() {
        return serverTypes;
    }

    public List<SecurityModeType> getSecurityModes() {
        return securityModes;
    }

    public List<MaskType> getMaskTypes() {
        return maskTypes;
    }
}
