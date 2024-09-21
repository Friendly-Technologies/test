package com.friendly.services.device.parameterstree.utils.supplier;

import static com.friendly.commons.models.device.setting.DeviceActionType.*;
import static com.friendly.commons.models.device.setting.DeviceActionType.DELETE;
import static com.friendly.services.device.parameterstree.utils.ParameterUtil.getShortName;

import com.friendly.commons.models.device.setting.DeviceActionType;
import com.friendly.commons.models.device.setting.DeviceObject;
import com.friendly.commons.models.device.setting.DeviceParameter;
import com.friendly.services.device.parameterstree.orm.acs.model.AbstractParameterEntity;
import com.friendly.services.device.parameterstree.orm.acs.repository.DeviceParameterRepository;
import com.friendly.services.settings.bootstrap.orm.acs.repository.ResourceDetailsLwm2mRepository;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceLWM2MTreeSupplier extends DeviceTreeSupplier {
    ResourceDetailsLwm2mRepository resourceDetailsLwm2mRepository;
    DeviceParameterRepository deviceParameterRepository;
    List<String> lwm2mNamesForAdding;
    List<String> methodNames;

  @Override
  public void init() {
        if (resourceDetailsLwm2mRepository != null) {
            lwm2mNamesForAdding = resourceDetailsLwm2mRepository.getLwm2mNamesForAdding(ownerId);
        }
        methodNames = parameterNameService.getMethodNames();
    }

    public boolean isObjectValid(String name) {
        String type = parameterNameService.getTypeById(paramMap.get(name).getNameId());
        return !"Resource Instance".equals(type);
    }

    @Override
    public String processNotValidObject(String fullName, DeviceObject parentObject, Iterator<String> it) {

        return handleLwm2mArray(fullName, parentObject, it);
    }

    public void postObjectWalk(DeviceObject deviceObject) {

    }

    @Override
    public DeviceParameter createTreeParam(String fullName, DeviceObject parentObject) {
        DeviceParameter deviceParameter = super.createTreeParam(fullName, parentObject);


        List<DeviceActionType> actions = new ArrayList<>();
        if (methodNames.contains(deviceParameter.getFullName())
                && (resourceDetailsLwm2mRepository != null &&
                !resourceDetailsLwm2mRepository.findByName(deviceParameter.getShortName()).isEmpty())) {
            actions.add(INVOKE);
        }

        if (Boolean.TRUE.equals(deviceParameter.getCanOverwrite())
                || deviceParameter.getType().equals("array")) {
            actions.add(DELETE);
        }

        if (resourceDetailsLwm2mRepository != null
            && (lwm2mNamesForAdding.contains(deviceParameter.getShortName()))) {
                    actions.add(ADD);
        }
    deviceParameter.setActions(actions);
        return deviceParameter;
    }

    private String handleLwm2mArray(String arrName, DeviceObject parentObject, Iterator<String> it) {
        List<DeviceParameter.Pair> valuePair = new ArrayList<>();
        try {
            while (it.hasNext()) {
                String name = it.next();
                if (!name.startsWith(arrName)) {
                    return name;
                }
                valuePair.add(new DeviceParameter.Pair(
                        Integer.valueOf(name.substring(name.lastIndexOf(".") + 1)),
                        paramMap.get(name).getValue()));
            }
        } finally {
            AbstractParameterEntity entity = paramMap.get(arrName);
            arrName = arrName.substring(0, arrName.length() - 1);
            List<DeviceActionType> actionsForArray = new ArrayList<>();
            actionsForArray.add(DELETE);
            DeviceParameter parameter = DeviceParameter.builder()
                    .id(entity.getId())
                    .nameId(entity.getNameId())
                    .shortName(getShortName(arrName))
                    .fullName(arrName)
                    .parentName(parentObject.getFullName().substring(0, parentObject.getFullName().length() - 1))
                    .type("array")
                    .actions(actionsForArray)
                    .canOverwrite(entity.getWriteable())
                    .value(valuePair.isEmpty() ? null : valuePair)
                    .build();
            parentObject.getParameters().add(parameter);
        }

        return null;
    }
}