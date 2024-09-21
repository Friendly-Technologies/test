package com.friendly.services.management.action.dto.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public enum ActionTypeEnum {
    GET_TASK("Get parameter"),
    SET_VALUE_TASK("Set parameter value"),
    SET_ATTRIBUTES_TASK("Policy"),
    REBOOT_TASK("Reboot"),
    FACTORY_RESET_TASK("Factory reset"),
    RPC_METHOD_TASK("Custom RPC"),
    RE_PROVISION_TASK("Device reprovision"),
    DOWNLOAD_TASK("Download file"),
    INSTALL_TASK("Install"),
    UPDATE_SOFTWARE_TASK("Update Software"),
    UNINSTALL_TASK("Uninstall"),
    DIAGNOSTIC_TASK("Diagnostics"),
    CALL_API_TASK("Call external API"),
    ADD_TO_PROVISION_TASK("Store in provision"),
    UPLOAD_TASK("Upload file"),
    BACKUP_TASK("Backup"),
    RESTORE_TASK("Restore"),
    ACTION_TASK("Action"),
    CPE_METHOD_TASK("Execute method");

    public static List<ActionTypeEnum> getEnums() {
        return ENUM_LIST;
    }

    /**
     * -- GETTER --
     *       SetParameter
     *  		Firmware
     *  		Action
     *  		Policy
     *  		UploadFile
     *  		GetParameter
     *  		Install
     *  		UpdateSoftware
     *  		Uninstall
     *  		Backup
     *  		Restore
     *  		Diagnostic
     *  		CallApi
     *  		StoreInProvision
     *
     */
    private final String description;


    private static final Map<String, ActionTypeEnum> DESCRIPTION_TO_ENUM_MAP = new HashMap<>();
    private static final List<ActionTypeEnum> ENUM_LIST = new ArrayList<>();

    static {
        for (ActionTypeEnum e : values()) {
            DESCRIPTION_TO_ENUM_MAP.put(e.description, e);
        }
        for (ActionTypeEnum e : values()) {
            if (ActionTypeEnum.ACTION_TASK.equals(e))
                continue;
            ENUM_LIST.add(e);
        }
    }


    ActionTypeEnum(String description) {
        this.description = description;
    }
}
