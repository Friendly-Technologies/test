package com.friendly.services.device.parameterstree.utils.supplier;

import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.setting.DeviceObject;
import com.friendly.commons.models.device.setting.DeviceParameter;
import com.friendly.services.device.parameterstree.service.ParameterNameService;
import com.friendly.services.device.parameterstree.utils.ParameterUtil;
import com.friendly.services.device.parameterstree.utils.helpers.IParameterHelper;
import com.friendly.services.device.parameterstree.orm.acs.model.AbstractParameterEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.friendly.commons.models.device.ProtocolType.MQTT;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class DeviceTreeSupplier extends AbstractTreeSupplier<DeviceParameter, DeviceObject> {
    private IParameterHelper parameterService;
    ParameterNameService parameterNameService;
    private List<String> names;
    private boolean fullTreeRequest;
    Map<String, AbstractParameterEntity> paramMap;
    Long ownerId;



    @Override
    public DeviceObject createTreeObj(String fullName, DeviceObject parentObject, ProtocolType protocol) {
        AbstractParameterEntity entity = paramMap.get(fullName);
        if (entity == null) {
            log.warn("Entity not found for name: " + fullName);
            return null;
        }
        DeviceObject deviceObject = super.createTreeObj(fullName, parentObject, protocol);

        boolean canAddObj = entity.getWriteable() != null && entity.getWriteable();
        boolean canDeleteObj = false;
        if (canAddObj) {
            canDeleteObj = fullName.matches(".*\\.\\d+\\.$");
            canAddObj = !canDeleteObj;
        }
        deviceObject.setId(entity.getId());
        deviceObject.setNameId(entity.getNameId());
        deviceObject.setCanDeleteObject(!MQTT.equals(protocol) && canDeleteObj);
        deviceObject.setCanAddObject(canAddObj);
        deviceObject.setItems(new ArrayList<>());
        deviceObject.setParameters(new ArrayList<>());
        return deviceObject;
    }

    @Override
    public DeviceParameter createTreeParam(String fullName, DeviceObject parentObject) {
        AbstractParameterEntity entity = paramMap.get(fullName);
        String value = entity.getValue();
        boolean isRef = ParameterUtil.isValueValidForReference(value)
                && (names.contains(value) || names.contains(value + ".")
                || (!fullTreeRequest && parameterService.isParamExist(entity.getCpeId(), value + ".")));

        String type = isRef ? "reference" : ParameterUtil.getType(parameterNameService.getTypeById(entity.getNameId()));
        DeviceParameter deviceParameter = super.createTreeParam(fullName, parentObject);
        deviceParameter.setId(entity.getId());
        deviceParameter.setNameId(entity.getNameId());
        deviceParameter.setParentName(parentObject.getFullName().substring(0, parentObject.getFullName().length() - 1));
        deviceParameter.setType(type);
        deviceParameter.setCanOverwrite(entity.getWriteable());
        if (value != null) {
            value = value.equals("device_params_sub") ? parameterService.getParameterExtendedValue(entity.getId()) : value;
            value = parameterService.decryptValueIfNeeded(value, entity.getNameId());
        }
        deviceParameter.setValue(value);
        deviceParameter.setActions(new ArrayList<>());
        return deviceParameter;
    }

    @Override
    public void postObjectWalk(DeviceObject deviceObject) {
        if (deviceObject.getCanAddObject()) {
            boolean canAddObj = deviceObject.getItems().stream().anyMatch(e -> e.getFullName().matches(".*\\.\\d+\\.$"));
            if (!canAddObj) {
                // !!!ForwardNumberOfEntries -> Device.DNS.Relay.Forwarding
                String nameNumberOfEntries = deviceObject.getFullName().substring(0, deviceObject.getFullName().length() - 1) + "NumberOfEntries";
                canAddObj = names.contains(nameNumberOfEntries)
                        || (!fullTreeRequest && parameterService.isParamExist(ownerId, nameNumberOfEntries));
            }
            deviceObject.setCanAddObject(canAddObj);
        }
    }

    @Override
    protected DeviceObject createTreeObj() {
        return DeviceObject.builder().build();
    }

    @Override
    protected DeviceParameter createTreeParam() {
        return DeviceParameter.builder().build();
    }
}