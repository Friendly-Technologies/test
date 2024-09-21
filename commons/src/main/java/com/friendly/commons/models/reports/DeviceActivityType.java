package com.friendly.commons.models.reports;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum DeviceActivityType {
    RESET_DEVICE("Reset_device","Reset device"),
    REBOOT_DEVICE("Reboot_device", "Reboot device"),
    DELETE_DEVICE("Delete_device", "Delete device"),
    CHANGE_PARAMETERS("Change_parameters", "Change parameters"),
    ADD_POLLING("Add_polling", "Add polling"), //not support yet
    REMOVE_POLLING("Remove_polling", "Remove polling"), //not support yet
    FILE_DOWNLOAD("File_download", "File download"),
    FILE_DELETED("File_deleted", "File deleted"),
    ADD_OBJECT("Add_object", "Add object"),
    DELETE_OBJECT("Delete_object", "Delete object"),
    ADD_UPLOAD("Add_upload", "Add upload"),
    DELETE_UPLOAD("Delete_upload", "Delete upload"),
    ADD_DIAGNOSTICS("Add_diagnostics", "Add diagnostics"),
    DELETE_DIAGNOSTICS("Delete_diagnostics", "Delete diagnostics"),
    CUSTOM_RPC("Custom_RPC", "Custom RPC"),
    REPROVISION("Reprovision", "Reprovision"),
    DEPLOYMENT_UNIT("Deployment_unit", "Deployment unit"), //not support yet
    TRACING("Tracing", "Tracing"),
    CHANGE_ATTRIBUTES("Change_attributes", "Change attributes"), //not support yet
    INVOKE_METHOD("Invoke_method", "Invoke cpe method"),
    DELETE_PARAMETER("Delete_parameter", "Delete parameter");

    private static final DeviceActivityType[] ALL_DEVICE_ACTIVITY_TYPES = DeviceActivityType.values();

    @JsonValue
    private final String name;
    private final String description;

    DeviceActivityType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static DeviceActivityType byName(String name) {
        if(StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("DeviceActivityType cannot be null");
        }

        Optional<DeviceActivityType> deviceActivityType = Arrays.stream(ALL_DEVICE_ACTIVITY_TYPES)
                .filter(t -> name.equalsIgnoreCase(t.getName()))
                .findFirst();

        return deviceActivityType.orElseThrow(
                () -> new IllegalArgumentException("No matching enum constant for the name: " + name));
    }
}
