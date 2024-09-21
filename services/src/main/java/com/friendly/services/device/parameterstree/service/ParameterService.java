package com.friendly.services.device.parameterstree.service;

import com.friendly.commons.cache.CpeParameterNameCache;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.GetNewParamsBody;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.response.ActiveConnectionResponse;
import com.friendly.commons.models.device.response.GetNewParams;
import com.friendly.commons.models.device.response.ParameterName;
import com.friendly.commons.models.device.setting.DeviceObject;
import com.friendly.commons.models.tree.TreeObject;
import com.friendly.commons.models.tree.TreeParameter;
import com.friendly.services.device.parameterstree.mapper.ParameterMapper;
import com.friendly.services.device.parameterstree.utils.ParameterUtil;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.infrastructure.config.jpa.DbConfig;
import com.friendly.services.uiservices.customization.TabUtil;
import com.friendly.services.device.parameterstree.utils.helpers.IParameterHelper;
import com.friendly.services.device.parameterstree.orm.acs.model.AbstractParameterEntity;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeParameterEntity;
import com.friendly.services.device.parameterstree.orm.acs.model.projections.CpeParameterCpeNameValueProjection;
import com.friendly.services.device.parameterstree.orm.acs.model.projections.CpeParameterNameIdValueProjection;
import com.friendly.services.device.parameterstree.utils.supplier.AbstractTreeSupplier;
import com.friendly.services.device.parameterstree.utils.supplier.DeviceLWM2MTreeSupplier;
import com.friendly.services.device.info.orm.acs.model.CpeEntity;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeParameterNameEntity;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.device.parameterstree.orm.acs.repository.DeviceCustomParameterRepository;
import com.friendly.services.device.parameterstree.orm.acs.repository.DeviceParameterRepository;
import com.friendly.services.settings.bootstrap.orm.acs.repository.ResourceDetailsLwm2mRepository;
import com.friendly.services.settings.bootstrap.orm.acs.repository.ResourceLwm2mRepository;
import com.friendly.services.device.parameterstree.utils.supplier.DeviceTreeSupplier;
import com.friendly.services.infrastructure.utils.LicenseUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import com.friendly.services.device.template.orm.acs.repository.DeviceTemplateRepository;

import static com.friendly.services.device.parameterstree.utils.ParameterUtil.getShortName;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParameterService implements IParameterHelper {
    @NonNull
    private final CpeParameterNameCache cpeParameterNameCache;
    @NonNull
    private final JwtService jwtService;
    @NonNull
    private final DeviceParameterRepository deviceParameterRepository;
    @NonNull
    private final DeviceCustomParameterRepository customParameterRepository;
    @NonNull
    private final CpeRepository cpeRepository;
    @NonNull
    private final ResourceDetailsLwm2mRepository resourceDetailsLwm2mRepository;

    @NonNull
    private final ResourceLwm2mRepository resourceLwm2mRepository;
    @NonNull
    final ParameterNameService parameterNameService;
    @NonNull
    final ParameterMapper parameterMapper;

    @NonNull
    final DeviceTemplateRepository deviceTemplateRepository;


    public boolean isParamExist(final Long deviceId, final String param) {
        Long id = parameterNameService.getIdByName(param);
        if (id == null) {
            return false;
        }
        String y = deviceParameterRepository.isParamExist(deviceId, id);
        return y != null && y.length() > 0;
    }


    public String getParamValue(Long deviceId, String param) {
        Long id = parameterNameService.getIdByName(param);
        if (id == null) {
            return null;
        }
        return getParamValue(deviceId, id);
    }

    public String getParamValue(Long deviceId, Long nameId) {
        return deviceParameterRepository.getParamValue(deviceId, nameId).orElse(null);
    }

    public boolean isValidUSPVersion(Long deviceId) {
        final String currentValue = getParamValue(deviceId, "Device.RootDataModelVersion");


        if (currentValue == null || currentValue.trim().isEmpty())
            return false;
        try {
            double ver = Double.parseDouble(currentValue);
            if (ver < 2.12) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String getRootParamName(final Long deviceId) {
        return isParamExistLike(deviceId, "InternetGatewayDevice.%")
                ? "InternetGatewayDevice."
                : isParamExistLike(deviceId, "Device.%") ? "Device." : "Root.";
    }

    public boolean isParamExistLike(final Long deviceId, final String param) {
        String y = DbConfig.isOracle() ? deviceParameterRepository.isParameterExistsLikeOracle(deviceId, param)
                : deviceParameterRepository.isParameterExistsLikeMysql(deviceId, param);

        return y != null && y.length() > 0;
    }

    public boolean isVoiceProfileEnable(Long deviceId) {
        return isParamExistLike(deviceId, "%.Services.VoiceService.1.VoiceProfile.%.Line.%.Enable");
    }

    public List<String> getParamNamesLike(final Long deviceId, final String param) {
        return deviceParameterRepository.getParamNamesLike(deviceId, param);
    }

    @Override
    public List<? extends AbstractParameterEntity> findAllByOwnerId(Long ownerId) {
        return findAllByCpeId(ownerId);
    }

    @Override
    public List<? extends AbstractParameterEntity> findAllByOwnerIdAndFullNameLike(Long ownerId, String name) {
        return findAllByCpeIdAndFullNameLike(ownerId, name);
    }

    public List<String> getParamValuesLike(final Long deviceId, final String param) {
        return deviceParameterRepository.getParamValuesLike(deviceId, param);
    }

    public boolean isAnyDiagnosticsExists(final Long deviceId) {
        String y = DbConfig.isOracle() ? deviceParameterRepository.isAnyDiagnosticsExistsOracle(deviceId)
                : deviceParameterRepository.isAnyDiagnosticsExistsMysql(deviceId);

        return y != null && y.length() > 0;
    }

    public List<CpeParameterEntity> findAllByCpeId(final Long cpeId) {
        return deviceParameterRepository.findAllByCpeId(cpeId);
    }

    public List<CpeParameterEntity> findAllByCpeIdAndFullNameLike(final Long cpeId, final String fullName) {
        return deviceParameterRepository.findAllByCpeIdAndFullNameLike(cpeId, fullName);
    }

    public ActiveConnectionResponse getActiveConnection(Long deviceId, String token) {
        jwtService.getSession(token);
        return new ActiveConnectionResponse(readActiveConnection(deviceId));
    }

    public String getDeviceLog(Long deviceId) {
        final List<String> logs;
        if (DbConfig.isOracle()) {
            logs = deviceParameterRepository.getDeviceLogOracle(deviceId)
                    .stream()
                    .map(ParameterUtil::clobToString)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            logs = deviceParameterRepository.getDeviceLogMySql(deviceId);
        }
        return String.join("\n", logs);
    }

    public boolean isLogsExistsByDeviceId(Long deviceId) {
        String y = DbConfig.isOracle() ? deviceParameterRepository.isParameterExtendedByDeviceIdOracle(deviceId)
                : deviceParameterRepository.isParameterExtendedByDeviceIdMysql(deviceId);

        return y != null && y.length() > 0;
    }

    public String readActiveConnection(Long deviceId) {
        String root = getRootParamName(deviceId);
        if (root.equals("Root.")) {
            return null;
        }

        URI uri = getActiveConnectionIp(deviceId, root);

        if (uri == null || ParameterUtil.isNoIp(uri.getHost()))
            return null;

        List<CpeParameterEntity> parameterEntities = deviceParameterRepository.findAllByCpeIdAndFullNameLike(deviceId, "%.ExternalIPAddress");
        if (root.equals("Device.") && parameterEntities.isEmpty())    // for TR-181
            parameterEntities = deviceParameterRepository.findAllByCpeIdAndFullNameLike(deviceId, "Device.IP.Interface.%.IPv%Address.%.IPAddress");

        if (parameterEntities.isEmpty())
            return null;

        parameterEntities = parameterEntities.stream().filter(e -> !ParameterUtil.isNoIp(e.getValue())).collect(Collectors.toList());

        CpeParameterEntity resultCR = null;
        CpeParameterEntity resultExt = null;
        CpeParameterEntity resultLocal = null;

        parameterEntities.sort((o1, o2) -> o1.getUpdated().isAfter(o2.getUpdated()) ? 1 : 0);

        for (CpeParameterEntity entity : parameterEntities) {
            if (StringUtils.isBlank(entity.getValue())) {
                continue;
            }
            if (entity.getValue().equals(uri.getHost())) {
                resultCR = entity;
                break;
            } else if (!ParameterUtil.isNoIp(entity.getValue()) && !ParameterUtil.isLocalIp(entity.getValue())) {
                if (resultExt == null) {
                    resultExt = entity;
                }
            } else if (!ParameterUtil.isNoIp(entity.getValue())) {
                if (resultLocal == null) {
                    resultLocal = entity;
                }
            }
        }
        CpeParameterEntity result = resultCR == null ? resultExt == null ? resultLocal : resultExt : resultCR;
        if (result == null) {
            return null;
        }
        return parameterNameService.getNameById(result.getNameId()).replace("ExternalIPAddress", "").replace("IPAddress", "");
    }

    public URI getActiveConnectionIp(Long deviceId, String root) {
        URI uri = null;
        try {
            String udpAddr = getParamValue(deviceId, root + "ManagementServer.UDPConnectionRequestAddress");
            String httpAddr = getParamValue(deviceId, root + "ManagementServer.ConnectionRequestURL");
            if (udpAddr != null && !udpAddr.isEmpty()) {
                try {
                    uri = new URI(udpAddr);
                } catch (Exception ignored) {
                }
            }
            if (uri == null && httpAddr != null && !httpAddr.isEmpty()) {
                uri = new URI(httpAddr);
            }
        } catch (Exception ignored) {
        }
        return uri;
    }


    public void deleteAllByCpeIdAndNameId(Long deviceId, Long nameId) {
        deviceParameterRepository.deleteAllByCpeIdAndNameId(deviceId, nameId);
    }

    public List<CpeParameterNameEntity> findGetAllNewParams(String objectName) {
        return deviceParameterRepository.findAllWritableNotObjectInstanceParamsLike(objectName);
    }

    public List<CpeParameterNameIdValueProjection> findNameIdValueByCpeIdAndNameIds(Long deviceId, List<Long> nameIds) {
        return deviceParameterRepository.findAllByCpeIdAndNameIdIn(deviceId, nameIds);
    }


    public List<DeviceObject> getDeviceParameters(final Long deviceId, final String fullName) {
        ProtocolType deviceProtocol = ProtocolType.fromValue(cpeRepository.getProtocolTypeByDevice(deviceId).orElse(0));
        long time = System.currentTimeMillis();
        final List<AbstractParameterEntity> entities = new ArrayList<>();

        if (StringUtils.isBlank(fullName)) {
            entities.addAll(customParameterRepository.findAllByCpeId(deviceId));
        } else {
            entities.addAll(customParameterRepository.findAllByCpeIdAndFullName(deviceId, fullName + "%"));
        }

        log.debug("Getting custom parameter tree for device: " + deviceId + " for parent name: " + fullName + " time: "  + (System.currentTimeMillis() - time));
        return getParameters(deviceId, deviceProtocol, this, resourceDetailsLwm2mRepository, fullName, entities);
    }

    public List<DeviceObject> getParameters(final Long ownerId, ProtocolType protocol,
                                            IParameterHelper parameterHelper,
                                            ResourceDetailsLwm2mRepository resourceDetailsLwm2mRepository,
                                            final String fullName,
                                            final List<AbstractParameterEntity> entities) {
        long time = System.currentTimeMillis();
        if (StringUtils.isBlank(fullName)) {
            entities.addAll(parameterHelper.findAllByOwnerId(ownerId));
        } else {
            entities.addAll(parameterHelper.findAllByOwnerIdAndFullNameLike(ownerId, fullName + "%"));
        }
        log.debug("Getting parameter tree for device: " + ownerId + " for parent name: " + fullName + " time: "  + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        Map<String, AbstractParameterEntity> paramMap =
                entities.stream()
                        .collect(Collectors.toMap(e ->
                                        parameterNameService.getNameById(e.getNameId()),
                                e -> e,
                                (existing, replacement) -> existing));
        log.debug("Getting parameter names for device tree: " + ownerId + " for parent name: " + fullName + " time: "  + (System.currentTimeMillis() - time));
        List<String> paramList = new ArrayList<>(paramMap.keySet());
        time = System.currentTimeMillis();
        resolveMissedObjectsAndSortTree(paramList, objName -> {
            String parentName = objName.substring(0, objName.lastIndexOf("."));
            parentName = parentName.contains(".") ? parentName.substring(0, parentName.lastIndexOf(".")) : "";
            paramMap.put(objName, CpeParameterEntity.builder()
                    .fullName(objName)
                    .shortName(ParameterUtil.getShortName(objName))
                    .parentName(parentName)
                    .writeable(false) // #2356: Extra add object button
                    .parameters(new ArrayList<>())
                    .build());
        });
        log.debug("resolveMissedObjectsAndSortTree for device tree: " + ownerId + " for parent name: " + fullName + " time: "  + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        DeviceObject rootObj = DeviceObject.builder()
                .fullName("")
                .items(new ArrayList<>())
                .parameters(new ArrayList<>())
                .build();

        DeviceTreeSupplier treeSupplier = protocol.equals(ProtocolType.LWM2M) ?
                DeviceLWM2MTreeSupplier.builder()
                        .parameterService(parameterHelper)
                        .parameterNameService(parameterNameService)
                        .resourceDetailsLwm2mRepository(resourceDetailsLwm2mRepository)
                        .fullTreeRequest(StringUtils.isBlank(fullName))
                        .paramMap(paramMap)
                        .names(paramList)
                        .ownerId(ownerId)
                        .build() :
                DeviceTreeSupplier.builder()
                        .parameterService(parameterHelper)
                        .parameterNameService(parameterNameService)
                        .fullTreeRequest(StringUtils.isBlank(fullName))
                        .paramMap(paramMap)
                        .ownerId(ownerId)
                        .names(paramList)
                        .build();
        treeSupplier.init();
        walkTree(rootObj, paramList.iterator(), treeSupplier, protocol);
        CpeParameterNameCache.printStatsAndReset();
        log.debug("walkTree for device tree: " + ownerId + " for parent name: " + fullName + " time: "  + (System.currentTimeMillis() - time));
        return rootObj.getItems();
    }

    public void resolveMissedObjectsAndSortTree(List<String> paramList, Consumer<String> objCreatedCallback) {

        paramList.sort(String::compareTo);

        Set<String> processedNames = new HashSet<>();
        List<String> newObjects = new ArrayList<>();
        for (String name : paramList) {
            String parentName = name;
            while (parentName.indexOf('.') != parentName.lastIndexOf('.')) {
                parentName = parentName.endsWith(".") ? parentName.substring(0, parentName.length() - 1) : parentName;
                parentName = parentName.contains(".") ? parentName.substring(0, parentName.lastIndexOf(".") + 1) : "";
                if (!processedNames.contains(parentName) && !parentName.isEmpty()) {
                    // create parent object
                    newObjects.add(parentName);
                    objCreatedCallback.accept(parentName);
                    processedNames.add(parentName);
                }
            }
            processedNames.add(name);
        }
        if (!newObjects.isEmpty()) {
            paramList.addAll(newObjects);
            paramList.sort(String::compareTo);
        }
    }

    <TreeParam extends TreeParameter, TreeObj extends TreeObject<TreeObj, TreeParam>> String walkTree(
            TreeObj parentObject,
            Iterator<String> it,
            AbstractTreeSupplier<TreeParam, TreeObj> treeSupplier,
            ProtocolType protocol) {
        try {
            String nextName = null;
            while (it.hasNext()) {
                String name = nextName == null ? it.next() : nextName;
                nextName = null;

                if (name.startsWith(parentObject.getFullName())) {
                    boolean isParameter = !name.endsWith(".");
                    if (isParameter) {
                        TreeParam parameter = treeSupplier.createTreeParam(name, parentObject);
                        parentObject.getParameters().add(parameter);
                    } else {
                        if (!treeSupplier.isObjectValid(name)) {
                            // LWM2M array
                            nextName = treeSupplier.processNotValidObject(name, parentObject, it);
                            continue;
                        }
                        TreeObj obj = treeSupplier.createTreeObj(name, parentObject, protocol);
                        if (obj == null) {
                            continue;
                        }
                        nextName = walkTree(obj, it, treeSupplier, protocol);
                        parentObject.getItems().add(obj);
                        treeSupplier.postObjectWalk(obj);
                    }
                } else {
                    return name;
                }
            }
        } finally {
            parentObject.getParameters().sort(Comparator.comparing(TreeParameter::getFullName, String.CASE_INSENSITIVE_ORDER));
            parentObject.getItems().sort(Comparator.comparing(TreeObject::getFullName, String.CASE_INSENSITIVE_ORDER));
        }

        return null;
    }

    <TreeParam extends TreeParameter, TreeObj extends TreeObject<TreeObj, TreeParam>> void walkTree2(
            TreeObj parentObject,
            Iterator<String> it,
            AbstractTreeSupplier<TreeParam, TreeObj> treeSupplier,
            ProtocolType protocol) {
//        try {
            String nextName = null;
            while (it.hasNext()) {
                String name = nextName == null ? it.next() : nextName;
                nextName = null;

                if (name.startsWith(parentObject.getFullName())) {
                    boolean isParameter = !name.endsWith(".");
                    if (isParameter) {
                        TreeParam parameter = treeSupplier.createTreeParam(name, parentObject);
                        parentObject.getParameters().add(parameter);
                    } else {
                        if (!treeSupplier.isObjectValid(name)) {
                            // LWM2M array
                            nextName = treeSupplier.processNotValidObject(name, parentObject, it);
                            continue;
                        }
                        TreeObj obj = treeSupplier.createTreeObj(name, parentObject, protocol);

                        parentObject.getItems().add(obj);
                        treeSupplier.postObjectWalk(obj);

                        parentObject = obj;
                    }
                } else {
                    parentObject = parentObject.getParent();
                    while (parentObject !=null && !name.startsWith(parentObject.getFullName())) {
                        parentObject = parentObject.getParent();
                    }
                    if (parentObject == null ) {
                        log.error("No parent for name: " + name);
                        return;
                    }
                }
            }
//        }
//        finally {
//            parentObject.getParameters().sort(Comparator.comparing(TreeParameter::getFullName, String.CASE_INSENSITIVE_ORDER));
//            parentObject.getItems().sort(Comparator.comparing(TreeObject::getFullName, String.CASE_INSENSITIVE_ORDER));
//        }

    }

    public String getMacAddress(Integer activeConnNameId, Map<String, String> nameValueMap) {
        String activeConn = activeConnNameId == null ? null : parameterNameService.getNameById(activeConnNameId);
        return ParameterUtil.getMacAddress(activeConn, nameValueMap);

    }

    public ParameterName nameToParameterName(String s) {
        String replace = s.replace(".i.", "._.");
        CpeParameterNameEntity entity = parameterNameService.findFirstByNameLike(replace);
        if (entity != null) {
            return parameterMapper.toParameterName(entity);
        }
        return null;
    }

    public List<ParameterName> getNewParams(Long deviceId, String objName) {
        int lastIndexOfDot = objName.lastIndexOf(".");

        if (lastIndexOfDot == -1) {
            return null;
        }

        if (deviceId != null) {
            Optional<CpeEntity> cpe = cpeRepository.findById(deviceId);

            if (cpe.isPresent()) {
                CpeEntity cpeEntity = cpe.get();
                if (cpeEntity.getProtocolId().equals(ProtocolType.TR069.ordinal())
                        || cpeEntity.getProtocolId().equals(ProtocolType.USP.ordinal())) {
                    String root = getRootParamName(cpeEntity.getId());
                    String name = objName + "i.";
                    List<String> params = TabUtil.getParameterListForObject(root, name, ClientType.sc);
                    if (params.isEmpty()) {
                        return Collections.emptyList();
                    }

                    return params.stream()
                            .filter(n -> !n.equals(name))
                            .map(this::nameToParameterName)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                }

            }
        }
        String shortName = ParameterUtil.getShortName(objName, false);
        Integer objectId = resourceLwm2mRepository.getObjectIdForNewParams(shortName);
        if (objectId == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(
                resourceLwm2mRepository.getShortNamesForNewParams(objectId)
                        .stream()
                        .map(name -> "%." + name)
                        .map(parameterNameService::getIdNameTypeByNameMask)
                        .flatMap(Collection::stream)
                        .map(parameterMapper::entityToParameterName)
                        .map(entity -> setTypeToArrayIfNeeded(objectId, entity))
                        .collect(Collectors.toMap(ParameterName::getShortName, e -> e,
                                (ex, rep) -> ex.getNameId() < rep.getNameId() ? ex : rep))
                        .values());

    }

    private ParameterName setTypeToArrayIfNeeded(Integer objectId, ParameterName param) {
        Integer instanceTypeForParamName
                = resourceLwm2mRepository.getInstanceTypeForParamName(param.getShortName(), objectId);
        if (instanceTypeForParamName == 1) {
            param.setType("array");
        }
        return param;
    }


    public Map<Long, Map<String, String>> findParameterNameValuesMapForCpes(List<Long> cpeIds, List<Long> nameIds) {
        List<CpeParameterCpeNameValueProjection> cpeParameterCpeNameValueProjections =
                deviceParameterRepository.findAllByCpeIdInAndNameIdIn(cpeIds, nameIds);

        Set<Long> uniqueNameIds = cpeParameterCpeNameValueProjections.stream()
                .map(CpeParameterCpeNameValueProjection::getNameId)
                .collect(Collectors.toSet());
        Map<Long, String> nameIdToNameMap = parameterNameService.getNamesByIds(new ArrayList<>(uniqueNameIds));
        Map<Long, Map<String, String>> map = new HashMap<>();
        for (CpeParameterCpeNameValueProjection o : cpeParameterCpeNameValueProjections) {
            map.computeIfAbsent(o.getCpeId(), k -> new HashMap<>())
                    .put(nameIdToNameMap.get(o.getNameId()), o.getValue());

        }
        return map;
    }


    public Map<Long, Map<String, String>> findDevicesPropertiesNameValuesMap(List<Long> cpeIds) {
        return findParameterNameValuesMapForCpes(cpeIds, ParameterNameService.getNameIdsForCpeProps());
    }

    public Map<String, String> findDevicePropertiesNameValuesMap(Long cpeId) {
        return findNameIdValueByCpeIdAndNameIds(cpeId, ParameterNameService.getNameIdsForCpeProps())
                .stream()
                .collect(Collectors.toMap(
                        p -> parameterNameService.getNameById(p.getNameId()),
                        p -> p.getValue() == null ? "" : p.getValue()));
    }

    public String getParameterExtendedValue(Long cpeParameterId) {
        String value = deviceParameterRepository.getParameterExtendedValueById(cpeParameterId);
        return value == null ? "" : value;
    }

    public String decryptValueIfNeeded(String value, Long nameId) {
        if (cpeParameterNameCache.isEncrypted(nameId.intValue())
                && LicenseUtils.checkIfValueShouldBeDecrypted(value)) {
            return LicenseUtils.decryptLicense(value);
        }
        return value;
    }
    public GetNewParams getNewParams(String token, GetNewParamsBody body) {
        jwtService.getSession(token);
        return new GetNewParams(
                getNewParams(body.getDeviceId().longValue(), body.getObjectName()));
    }
}
