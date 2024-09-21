package com.friendly.services.management.action.utils;

import com.friendly.commons.models.device.FTTaskTypesEnum;
import com.friendly.services.device.info.mapper.DeviceMapper;
import com.friendly.services.device.info.service.DeviceSoftwareService;
import com.friendly.services.management.action.utils.converter.ActionRestoreConverter;
import com.friendly.services.device.diagnostics.service.DeviceDiagnosticsService;
import com.friendly.services.management.action.utils.converter.ActionCallApiConverter;
import com.friendly.services.management.action.utils.converter.ActionChangeDUStateConverter;
import com.friendly.services.management.action.utils.converter.ActionConverter;
import com.friendly.services.management.action.utils.converter.ActionCpeMethodConverter;
import com.friendly.services.management.action.utils.converter.ActionDiagnosticConverter;
import com.friendly.services.management.action.utils.converter.ActionDownloadConverter;
import com.friendly.services.management.action.utils.converter.ActionFactoryResetConverter;
import com.friendly.services.management.action.utils.converter.ActionGetTaskConverter;
import com.friendly.services.filemanagement.orm.acs.repository.FileTypeRepository;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.management.profiles.orm.acs.repository.DeviceProfileParameterNotificationRepository;
import com.friendly.services.management.profiles.orm.acs.repository.DeviceProfileRepository;
import com.friendly.services.device.parameterstree.service.ParameterNameService;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.template.service.TemplateService;
import com.friendly.services.management.action.utils.converter.ActionAddToProvisionConverter;
import com.friendly.services.management.action.utils.converter.ActionBackupConverter;
import com.friendly.services.management.action.utils.converter.ActionInstallOpConverter;
import com.friendly.services.management.action.utils.converter.ActionRebootTaskConverter;
import com.friendly.services.management.action.utils.converter.ActionReprovisionConverter;
import com.friendly.services.management.action.utils.converter.ActionRpcMethodConverter;
import com.friendly.services.management.action.utils.converter.ActionSetAttributesConverter;
import com.friendly.services.management.action.utils.converter.ActionSetValueConverter;
import com.friendly.services.management.action.utils.converter.ActionUninstallOpConverter;
import com.friendly.services.management.action.utils.converter.ActionUpdateOpConverter;
import com.friendly.services.management.action.utils.converter.ActionUploadConverter;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.settings.fileserver.FileServerService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;

@Component
@AllArgsConstructor
@Getter
public class ActionUtils {
    private final ParameterNameService parameterNameService;
    private final ParameterService parameterService;
    private final DeviceMapper deviceMapper;
    private final DeviceProfileParameterNotificationRepository deviceProfileParameterNotificationRepository;
    private final FileTypeRepository fileTypeRepository;
    private final FileServerService fileServerService;
    private final TemplateService templateService;
    private final DeviceProfileRepository deviceProfileRepository;
    private final ProductClassGroupRepository productClassGroupRepository;
    private final DeviceDiagnosticsService deviceDiagnosticsService;
    private final DeviceSoftwareService deviceSoftwareService;


    public static final Map<ActionTypeEnum, ActionConverter<?, ?>> CONVERTER_MAP_FOR_ACTION_TYPE_ENUM = new EnumMap<>(ActionTypeEnum.class);
    public static final Map<FTTaskTypesEnum, ActionConverter<?, ?>> CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM = new EnumMap<>(FTTaskTypesEnum.class);

    @PostConstruct
    public void initConverterMapForActionTypeEnum() {

        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.CALL_API_TASK, new ActionCallApiConverter());
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.UPLOAD_TASK, new ActionUploadConverter(fileTypeRepository, fileServerService,
                templateService, deviceProfileRepository));
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.DIAGNOSTIC_TASK, new ActionDiagnosticConverter(parameterNameService, deviceDiagnosticsService));
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.DOWNLOAD_TASK, new ActionDownloadConverter(deviceMapper, fileTypeRepository, fileServerService));
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.RE_PROVISION_TASK, new ActionReprovisionConverter());
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.RPC_METHOD_TASK, new ActionRpcMethodConverter());
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.SET_ATTRIBUTES_TASK, new ActionSetAttributesConverter(parameterNameService));
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.SET_VALUE_TASK, new ActionSetValueConverter(parameterNameService));
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.GET_TASK, new ActionGetTaskConverter(parameterNameService));
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.FACTORY_RESET_TASK, new ActionFactoryResetConverter());
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.INSTALL_TASK, new ActionInstallOpConverter(fileServerService));
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.UNINSTALL_TASK, new ActionUninstallOpConverter(deviceSoftwareService));
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.UPDATE_SOFTWARE_TASK, new ActionUpdateOpConverter(deviceSoftwareService, fileServerService));
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.REBOOT_TASK, new ActionRebootTaskConverter());
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.CPE_METHOD_TASK, new ActionCpeMethodConverter());
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.BACKUP_TASK, new ActionBackupConverter(fileServerService));
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.RESTORE_TASK, new ActionRestoreConverter(fileServerService));
        CONVERTER_MAP_FOR_ACTION_TYPE_ENUM.put(ActionTypeEnum.ADD_TO_PROVISION_TASK, new ActionAddToProvisionConverter(deviceProfileParameterNotificationRepository));
    }

    @PostConstruct
    public void initConverterMapForFTTaskTypeEnum() {
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.CallApiAction, new ActionCallApiConverter());
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.UploadUpdateGroup, new ActionUploadConverter(fileTypeRepository, fileServerService,
                templateService, deviceProfileRepository));
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.DiagnosticUpdateGroup, new ActionDiagnosticConverter(parameterNameService, deviceDiagnosticsService));
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.DownloadUpdateGroup, new ActionDownloadConverter(deviceMapper, fileTypeRepository, fileServerService));
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.ReProvisionUpdateGroup, new ActionReprovisionConverter());
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.CustomRPCUpdateGroup, new ActionRpcMethodConverter());
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.SetParameterAttributesUpdateGroup, new ActionSetAttributesConverter(parameterNameService));
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.SetParameterValuesUpdateGroup, new ActionSetValueConverter(parameterNameService));
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.GetParamUpdateGroup, new ActionGetTaskConverter(parameterNameService));
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.FactoryReset, new ActionFactoryResetConverter());
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.ChangeDUStateUpdateGroup, new ActionChangeDUStateConverter());
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.Reboot, new ActionRebootTaskConverter());
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.ExecuteMethod, new ActionCpeMethodConverter());
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.BackupCpeConfiguration, new ActionBackupConverter(fileServerService));
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.RestoreCpeConfiguration, new ActionRestoreConverter(fileServerService));
        CONVERTER_MAP_FOR_FT_TASK_TYPE_ENUM.put(FTTaskTypesEnum.AddToProvisionAction, new ActionAddToProvisionConverter(deviceProfileParameterNotificationRepository));
    }


}
