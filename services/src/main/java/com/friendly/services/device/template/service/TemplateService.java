package com.friendly.services.device.template.service;

import com.friendly.commons.models.device.ModelManufacturerRequest;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.UpdateDeviceTemplateBody;
import com.friendly.commons.models.device.response.DeviceObjectsResponse;
import com.friendly.commons.models.device.response.IsExistResponse;
import com.friendly.commons.models.device.setting.DeviceObject;
import com.friendly.commons.models.tabs.TemplateParametersBody;
import com.friendly.services.device.info.orm.acs.repository.DeviceRepository;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.infrastructure.config.jpa.DbConfig;
import com.friendly.services.device.parameterstree.orm.acs.model.AbstractParameterEntity;
import com.friendly.services.device.method.orm.acs.model.CpeMethodNameEntity;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import com.friendly.services.device.template.orm.acs.repository.DeviceTemplateRepository;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.device.parameterstree.service.ParameterNameService;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.parameterstree.utils.helpers.IParameterHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateService implements IParameterHelper {
    @NonNull
    private final DeviceTemplateRepository deviceTemplateRepository;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    final ParameterNameService parameterNameService;

    @NonNull
    final ProductClassGroupRepository productClassGroupRepository;

    @NonNull
    final ParameterService parameterService;

    final DeviceRepository deviceRepository;

    public void replaceParametersTemplate(Integer deviceId, Integer groupId) {
        deviceTemplateRepository.deleteByGroupId(groupId);
        deviceTemplateRepository.updateDeviceTemplate(deviceId, groupId);
    }


    public boolean isParamExist(final Long groupId, final String param) {
        Long id = parameterNameService.getIdByName(param);
        if (id == null) {
            return false;
        }
        String y = deviceTemplateRepository.isParamExist(groupId, id);
        return y != null && y.length() > 0;
    }

    @Transactional
    public void updateDeviceTemplate(String token, UpdateDeviceTemplateBody body) {
        jwtService.getSession(token);
        Integer deviceId = body.getDeviceId();
        Integer groupId = deviceRepository.getProductGroupIdByDeviceId(deviceId);

        if (groupId != null) {
            replaceParametersTemplate(deviceId, groupId);

            deviceRepository.deleteOldTemplateMethod(groupId);
            deviceRepository.updateDeviceTemplateMethod(deviceId, groupId);
        }
    }

    public String getRootParamName(Long groupId) {
        return isParamExistLike(groupId, "InternetGatewayDevice.%")
                ? "InternetGatewayDevice."
                : isParamExistLike(groupId, "Device.%") ? "Device." : "Root.";
    }

    public boolean isParamExistLike(final Long groupId, final String param) {
        String y = DbConfig.isOracle() ? deviceTemplateRepository.isParameterExistsLikeOracle(groupId, param)
                : deviceTemplateRepository.isParameterExistsLikeMysql(groupId, param);

        return y != null && y.length() > 0;
    }

    public boolean isParamExistInTemplateLike(final Long groupId, final String param) {
        String y = DbConfig.isOracle() ? deviceTemplateRepository.isParameterExistsLikeOracle(groupId, param)
                : deviceTemplateRepository.isParameterExistsLikeMysql(groupId, param);

        return y != null && y.length() > 0;
    }

    public boolean isVoiceProfileEnableInTemplate(Long groupId) {
        return isParamExistInTemplateLike(groupId, "%.Services.VoiceService.1.VoiceProfile.%.Line.%.Enable");
    }

    public List<String> getParamNamesLike(final Long groupId, final String param) {
        return deviceTemplateRepository.getParamNamesLike(groupId, param);
    }

    @Override
    public List<? extends AbstractParameterEntity> findAllByOwnerId(Long ownerId) {
        return deviceTemplateRepository.findAllByGroupId(ownerId);
    }

    @Override
    public List<? extends AbstractParameterEntity> findAllByOwnerIdAndFullNameLike(Long ownerId, String name) {
        return deviceTemplateRepository.findAllByGroupIdAndFullNameLike(ownerId, name);
    }

    public DeviceObjectsResponse getGroupParameters(final String token, final TemplateParametersBody body) {
        jwtService.getSession(token);

        return new DeviceObjectsResponse(getGroupParameters(body.getManufacturer(), body.getModel(), body.getFullName()));
    }
    public List<DeviceObject> getGroupParameters(final String manufacturer, final String model, final String fullName) {
        Optional<ProductClassGroupEntity> opt = productClassGroupRepository.findByManufacturerNameAndModel(manufacturer, model);
        if (!opt.isPresent()) {
            log.error("Manufacturer {} and model {} don't exist", manufacturer, model);
            return Collections.emptyList();
        }
        return getGroupParameters(opt.get(), fullName);
    }

    public List<DeviceObject> getGroupParameters(ProductClassGroupEntity productClassGroup, final String fullName) {
        ProtocolType deviceProtocol = ProtocolType.fromValue(productClassGroup.getProtocolId());
        return parameterService.getParameters(productClassGroup.getId(), deviceProtocol, this,
                null, fullName, new ArrayList<>());
    }

    public List<DeviceObject> getGroupParametersByGroupId(final Long groupId, ProtocolType deviceProtocol, final String fullName) {
        return parameterService.getParameters(groupId, deviceProtocol, this,
                null, fullName, new ArrayList<>());
    }

    public List<String> getMethodNameEntities(final Long groupId) {
        final List<CpeMethodNameEntity> methodNames = deviceTemplateRepository.getCpeMethodNameEntityByGroupId(groupId);
        final List<String> actionTypeEnums = new ArrayList<>();
        methodNames.forEach(e -> actionTypeEnums.add(e.getName()));
        return actionTypeEnums;
    }

    public boolean isAnyDiagnosticsExists(final Long groupId) {
        String y = DbConfig.isOracle() ? deviceTemplateRepository.isAnyDiagnosticsExistsOracle(groupId)
                : deviceTemplateRepository.isAnyDiagnosticsExistsMysql(groupId);

        return y != null && y.length() > 0;
    }

    public String getRootParamNameFromTemplate(final Long groupId) {
        return isParamExistInTemplateLike(groupId, "InternetGatewayDevice.%")
                ? "InternetGatewayDevice."
                : isParamExistLike(groupId, "Device.%") ? "Device." : "Root.";
    }


    public List<String> getWriteParamNamesLike(Long groupId, String param) {
        return deviceTemplateRepository.getWriteParamNamesLike(groupId, param);
    }

    @Override
    public String decryptValueIfNeeded(String value, Long nameId) {
        return value;
    }

    @Override
    public String getParameterExtendedValue(Long cpeParameterId) {
        return null;
    }

    public IsExistResponse checkIfTemplateExists(String token, ModelManufacturerRequest request) {
        jwtService.getSession(token);

        List<ProductClassGroupEntity> list = productClassGroupRepository.findAllByManufacturerNameAndModel(request.getManufacturer(), request.getModel());

        if(list == null || list.isEmpty()) {
            return new IsExistResponse(false);
        }

        return new IsExistResponse(!deviceTemplateRepository.checkIfTemplateExists(list.get(0).getId()).isEmpty());
    }
}