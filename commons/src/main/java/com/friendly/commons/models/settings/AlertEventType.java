package com.friendly.commons.models.settings;

/**
 * Enum that defines a Alert Events Type
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
public enum AlertEventType {

    ACS_CONNECTION,
    DB_CONNECTION,
    //LICENCE_WILL_EXPIRE,
    LICENCE_EXPIRE,
    LICENCE_HAS_EXPIRED,
    DIFFER_ACS_TIME,
    DENIED_ACCESS_ALL_DEVICES,
    USED_90_LIMIT_ALL_DEVICES,
    LIMIT_ALL_DEVICES,
    DENIED_ACCESS_TR069,
    //USED_90_LIMIT_TR069,
    LIMIT_TR069,
    DENIED_ACCESS_LWM2M,
    //USED_90_LIMIT_LWM2M,
    LIMIT_LWM2M,
    DENIED_ACCESS_MQTT,
    //USED_90_LIMIT_MQTT,
    LIMIT_MQTT,
    DENIED_ACCESS_USP,
    //USED_90_LIMIT_USP,
    LIMIT_USP

}
