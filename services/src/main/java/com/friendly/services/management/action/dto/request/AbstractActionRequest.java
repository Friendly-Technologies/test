package com.friendly.services.management.action.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.BackupTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.DiagnosticTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.RestoreTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.changedustate.InstallOpTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.changedustate.UninstallOpTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.changedustate.UpdateOpTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.getparam.GetTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.AddToProvisionTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.CallApiTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.DownloadTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.FactoryResetTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.RebootTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.ReprovisionTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.RpcMethodTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.UploadTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.cpemethod.CpeMethodTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.setattrib.SetAttributesTaskAction;
import com.friendly.services.management.action.dto.request.inheritors.setvalue.SetValueTaskAction;
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
        property = AbstractActionRequest.ACTION_TYPE_PROPERTY_NAME,
        defaultImpl = AbstractActionRequest.class,
        visible = true)
@JsonSubTypes({
        /* Names for sub-type mapping should be gotten from {@link ActionTypeEnum} enum */
        @JsonSubTypes.Type(name = "GET_TASK", value = GetTaskAction.class),
        @JsonSubTypes.Type(name = "SET_VALUE_TASK", value = SetValueTaskAction.class),
        @JsonSubTypes.Type(name = "SET_ATTRIBUTES_TASK", value = SetAttributesTaskAction.class),
        @JsonSubTypes.Type(name = "REBOOT_TASK", value = RebootTaskAction.class),
        @JsonSubTypes.Type(name = "FACTORY_RESET_TASK", value = FactoryResetTaskAction.class),
        @JsonSubTypes.Type(name = "RPC_METHOD_TASK", value = RpcMethodTaskAction.class),
        @JsonSubTypes.Type(name = "RE_PROVISION_TASK", value = ReprovisionTaskAction.class),
        @JsonSubTypes.Type(name = "DOWNLOAD_TASK", value = DownloadTaskAction.class),
        @JsonSubTypes.Type(name = "INSTALL_TASK", value = InstallOpTaskAction.class),
        @JsonSubTypes.Type(name = "UNINSTALL_TASK", value = UninstallOpTaskAction.class),
        @JsonSubTypes.Type(name = "UPDATE_SOFTWARE_TASK", value = UpdateOpTaskAction.class),
        @JsonSubTypes.Type(name = "DIAGNOSTIC_TASK", value = DiagnosticTaskAction.class),
        @JsonSubTypes.Type(name = "CALL_API_TASK", value = CallApiTaskAction.class),
        @JsonSubTypes.Type(name = "ADD_TO_PROVISION_TASK", value = AddToProvisionTaskAction.class),
        @JsonSubTypes.Type(name = "UPLOAD_TASK", value = UploadTaskAction.class),
        @JsonSubTypes.Type(name = "BACKUP_TASK", value = BackupTaskAction.class),
        @JsonSubTypes.Type(name = "CPE_METHOD_TASK", value = CpeMethodTaskAction.class),
        @JsonSubTypes.Type(name = "RESTORE_TASK", value = RestoreTaskAction.class)
})
public abstract class AbstractActionRequest implements Serializable {
    static final String ACTION_TYPE_PROPERTY_NAME = "taskType";
    private ActionTypeEnum taskType;
    private Integer order;
}
