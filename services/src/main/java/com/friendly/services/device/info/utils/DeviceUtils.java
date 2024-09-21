package com.friendly.services.device.info.utils;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.Device;
import com.friendly.commons.models.device.DeviceConfig;
import com.friendly.commons.models.device.DeviceConfigType;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.rpc.RpcMethod;
import com.friendly.commons.models.device.setting.DeviceSimplifiedParams;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.view.AbstractView;
import com.friendly.commons.models.view.ConditionLogic;
import com.friendly.commons.models.view.ConditionType;
import com.friendly.commons.models.view.ListView;
import com.friendly.commons.models.view.ViewColumn;
import com.friendly.commons.models.view.ViewCondition;
import com.friendly.services.device.info.orm.acs.repository.DeviceRepository;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.uiservices.customization.Customization;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeParameterNameEntity;
import com.friendly.services.device.parameterstree.service.ParameterNameService;
import com.friendly.services.uiservices.view.ViewService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.DEVICE_NOT_FOUND;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceUtils {
    @NonNull ParameterNameService parameterNameService;
    @NonNull ViewService viewService;
    private final JwtService jwtService;
    private final  DeviceRepository deviceRepository;
    private final DomainService domainService;

    public boolean validateDeviceDomain(final String token, final Long id) {
        Session session = jwtService.getSession(token);
        Integer domainId = domainService.getDomainIdByUserId(session.getUserId()).orElse(0);
        if (domainId == 0 || !domainService.isDomainsEnabled(session.getClientType())) {
            if(!deviceRepository.existsById(id)) {
                throw new FriendlyEntityNotFoundException(DEVICE_NOT_FOUND, id);
            }
            return true;
        }
        final Integer deviceDomainId =
                deviceRepository
                        .getDomainIdById(id)
                        .orElseThrow(() -> new FriendlyEntityNotFoundException(DEVICE_NOT_FOUND, id));

        List<Integer> userDomainIds =
                domainService.getDomainIdsByName(domainService.getDomainNameById(domainId));
        return userDomainIds.stream().anyMatch(d -> d.equals(deviceDomainId));
    }
    public void fillParameterMapping(Map<String, List<String>> map) {
        if (!map.isEmpty()) {
            DeviceViewUtil.fillParameterMapping(map);
            DeviceViewUtil.PARAMETER_NAME_SEARCH_IDS.putAll(DeviceViewUtil.PARAMETER_NAMES_SEARCH.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            e -> parameterNameService.getIdsByNames(e.getValue()
                                    .stream()
                                    .filter(n -> !n.contains("%")).collect(Collectors.toList())))));
            List<String> namePatterns = DeviceViewUtil.PARAMETER_NAMES_SEARCH.values()
                    .stream().flatMap(Collection::stream)
                    .distinct()
                    .collect(Collectors.toList());
            ParameterNameService.nameIdsForCpeProps = parameterNameService.findAll((Specification<CpeParameterNameEntity>)
                            (root, cq, cb) -> cb.or(namePatterns
                                    .stream()
                                    .map(n -> cb.like(root.get("name"), n))
                                    .toArray(Predicate[]::new)))
                    .stream().map(CpeParameterNameEntity::getId).collect(Collectors.toList());
            int a =0;
        }
    }

    public static List<DeviceConfig> getDeviceConfig(final DeviceConfigType type) {
        return Customization.getDeviceConfigForClient(ClientType.sc).getOrDefault(type, Collections.emptyList());
    }

    public static Integer convertProtocolTypeToId(final ProtocolType protocolType) {
        if (protocolType == null) {
            return null;
        }
        return protocolType.getValue();
    }


    public static List<RpcMethod> getRpcMethods(ClientType clientType, final String rootName) {
        return Customization.getRpcMethodsMapForClient(clientType).get(rootName);
    }

    public static List<DeviceSimplifiedParams> getDeviceSimplifiedParams(ClientType clientType, final String root) {
        return Customization.getSimplifiedViewMapForClient(clientType).get(root);
    }

    public static Object[] getDefaultEntityObjects(final Device device) {
        return new Object[]{device.getDomainName(), device.getCreated(), device.getStatus(), device.getSerial(),
                device.getUpdated(), device.getFirmware(), device.getProtocolType(), device.getManufacturer(),
                device.getModel(), device.getOui(), device.getUserLogin(), device.getUserName(), device.getPhone(),
                device.getZip(), device.getUserLocation(), device.getUserTag(), device.getUserStatus(), device.getUserId(),
                device.getHardware(), device.getSoftware(), device.getIpAddress(), device.getMacAddress(), device.getUptime(),
                device.getCust1(), device.getCust2(), device.getCust3(), device.getCust4(), device.getCust5(),
                device.getCust6(), device.getCust7(), device.getCust8(), device.getCust9(), device.getCust10(),
                device.getCompletedTasks(), device.getFailedTasks(), device.getPendingTasks(), device.getRejectedTasks(),
                device.getSentTasks()};
    }

    public static List<ViewCondition> parseFromParams(List<HashMap<String, Object>> par) {
        if (par == null) {
            return null;
        }
        return par.stream().
                map(p -> ViewCondition.builder()
                        .id(p.get("id") != null ? (Long) p.get("id") : null)
                        .columnKey(p.get("columnKey") != null ? (String) p.get("columnKey") : null)
                        .logic(p.get("logic") != null ? ConditionLogic.valueOf((String) p.get("logic")) : null)
                        .compare(p.get("compare") != null ? ConditionType.valueOf((String) p.get("compare")) : null)
                        .columnName(p.get("compareName") != null ? (String) p.get("compareName") : null)
                        .conditionString(safeToString(p.get("conditionString")))
                        .conditionDateIso(p.get("conditionDateIso") != null ? (Instant) p.get("conditionDateIso") : null)
                        .conditionDate(p.get("conditionDate") != null ? (String) p.get("conditionDate") : null)
                        .items(p.get("items") != null ? parseFromParams((List<HashMap<String, Object>>) p.get("items")) : null)
                        .build())
                .collect(Collectors.toList());
    }

    private static String safeToString(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof Integer) {
            return Integer.toString((Integer) obj);
        } else {
            return null;
        }
    }

    public Object[] getEntityObjectsFromViewColumns(final Device device, final AbstractView view) {
        if (view instanceof ListView) {
            final List<ViewColumn> viewColumns = ((ListView) view).getColumns();
            final Object[] object = new Object[viewColumns.size()];

            viewColumns.forEach(c -> object[viewColumns.indexOf(c)] =
                    ColumnMapper.matchDeviceParamToColumnName(device, c.getColumnKey()));

            return object;
        }

        return DeviceUtils.getDefaultEntityObjects(device);
    }

    public AbstractView getAbstractView(final Long viewId, final Session session) {
        return viewService.getAbstractView(viewId, session);
    }
}
