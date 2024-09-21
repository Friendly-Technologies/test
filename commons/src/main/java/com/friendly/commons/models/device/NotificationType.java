package com.friendly.commons.models.device;

public enum NotificationType {
    TR_OFF(ProtocolType.TR069, 0, "Off"),
    TR_PASSIVE(ProtocolType.TR069, 1, "Passive"),
    TR_ACTIVE(ProtocolType.TR069, 2, "Active"),
    TR_RESET(ProtocolType.TR069, 3, "Reset"),
    LW_ON_1(ProtocolType.LWM2M, 1, "On"),
    LW_ON_2(ProtocolType.LWM2M, 2, "On"),
    LW_OFF_1(ProtocolType.LWM2M, 0, "Off"),
    LW_OFF_2(ProtocolType.LWM2M, 3, "Off"),
    UNKNOWN(ProtocolType.TR069, 100, "");

    private final ProtocolType protocolType;
    private final int value;
    private final String description;

    NotificationType(ProtocolType protocolType, int value, String description) {
        this.protocolType = protocolType;
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public ProtocolType getProtocolType() {
        return protocolType;
    }

    public static NotificationType fromValue(ProtocolType protocolType, int value) {
        for (NotificationType notificationType : values()) {
            if (notificationType.getProtocolType().equals(protocolType) && notificationType.getValue() == value) {
                return notificationType;
            }
        }
        return UNKNOWN;
    }
}
