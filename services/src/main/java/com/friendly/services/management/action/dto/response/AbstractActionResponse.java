package com.friendly.services.management.action.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.response.diagnostictask.DiagnosticTaskActionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;


@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = AbstractActionResponse.ACTION_TYPE_PROPERTY_NAME,
        defaultImpl = AbstractActionResponse.class,
        visible = true)
@JsonSubTypes({
        /* Names for sub-type mapping should be gotten from {@link ActionTypeEnum} enum */
        @JsonSubTypes.Type(name = "GET_TASK", value = GetTaskActionResponse.class),
        @JsonSubTypes.Type(name = "SET_VALUE_TASK", value = SetValueTaskActionResponse.class),
        @JsonSubTypes.Type(name = "SET_ATTRIBUTES_TASK", value = SetAttributesTaskActionResponse.class),
//        @JsonSubTypes.Type(name = "REBOOT_TASK", value = ),
//        @JsonSubTypes.Type(name = "FACTORY_RESET_TASK", value = ),
        @JsonSubTypes.Type(name = "RPC_METHOD_TASK", value = RpcMethodResponse.class),
        @JsonSubTypes.Type(name = "RE_PROVISION_TASK", value = ReprovisionTaskActionResponse.class),
        @JsonSubTypes.Type(name = "DOWNLOAD_TASK", value = DownloadTaskActionResponse.class),
        @JsonSubTypes.Type(name = "INSTALL_TASK", value = InstallOpResponse.class),
        @JsonSubTypes.Type(name = "UNINSTALL_TASK", value = UninstallOpResponse.class),
        @JsonSubTypes.Type(name = "UPDATE_SOFTWARE_TASK", value = UpdateOpResponse.class),
        @JsonSubTypes.Type(name = "DIAGNOSTIC_TASK", value = DiagnosticTaskActionResponse.class),
        @JsonSubTypes.Type(name = "CALL_API_TASK", value = CallApiTaskActionResponse.class),
//        @JsonSubTypes.Type(name = "ADD_TO_PROVISION_TASK", value = ),
        @JsonSubTypes.Type(name = "UPLOAD_TASK", value = UploadTaskActionResponse.class),
        @JsonSubTypes.Type(name = "BACKUP_TASK", value = BackupTaskActionResponse.class),
        @JsonSubTypes.Type(name = "CPE_METHOD_TASK", value = CpeMethodResponse.class),
        @JsonSubTypes.Type(name = "RESTORE_TASK", value = RestoreTaskActionResponse.class)
})
public abstract class AbstractActionResponse implements Serializable {
    static final String ACTION_TYPE_PROPERTY_NAME = "taskType";
    @JsonIgnore
    private ActionTypeEnum taskType;
}
