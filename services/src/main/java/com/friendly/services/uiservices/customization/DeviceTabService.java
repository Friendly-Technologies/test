package com.friendly.services.uiservices.customization;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.Device;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.response.DeviceObjectsSimpleResponse;
import com.friendly.commons.models.device.response.DeviceTabsResponse;
import com.friendly.commons.models.device.setting.DeviceObjectSimple;
import com.friendly.commons.models.device.setting.DeviceParameterSimple;
import com.friendly.commons.models.device.setting.DeviceSimplifiedParams;
import com.friendly.commons.models.device.setting.DeviceTabView;
import com.friendly.commons.models.device.setting.TabViewType;
import com.friendly.commons.models.tabs.DeviceTab;
import com.friendly.commons.models.tabs.DeviceTabViewBody;
import com.friendly.commons.models.tabs.TaskTypeTabView;
import com.friendly.commons.models.tree.TreeTab;
import com.friendly.commons.models.user.Session;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.device.activity.service.DeviceActivityService;
import com.friendly.services.device.info.service.DeviceSoftwareService;
import com.friendly.services.device.provision.service.DeviceProvisionService;
import com.friendly.services.device.info.utils.DeviceUtils;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import com.friendly.services.device.method.orm.acs.repository.CpeMethodRepository;
import com.friendly.services.device.info.orm.acs.repository.CpeRepository;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.device.history.orm.acs.repository.CpeLogEventNameEntityRepository;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.friendly.services.device.parameterstree.utils.ParameterUtil;
import com.friendly.services.device.template.service.TemplateService;
import com.friendly.services.device.parameterstree.utils.helpers.IParameterHelper;
import com.friendly.services.management.profiles.entity.deviceprofile.DeviceProfileAutomationEventsNamesResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.friendly.commons.models.tabs.TaskTypeTabView.GET_TASK;
import static com.friendly.commons.models.tabs.TaskTypeTabView.SET_ATTRIBUTES_TASK;
import static com.friendly.commons.models.tabs.TaskTypeTabView.SET_VALUE_TASK;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.CLIENT_TYPES_ARE_NOT_COMPATIBLE;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.PRODUCT_CLASS_GROUP_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.TAB_NOT_FOUND;

/**
 * Service that exposes the base functionality for interacting with {@link Device} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceTabService {

    @NonNull
    private final DeviceProvisionService provisionService;

    @NonNull
    private final DeviceActivityService activityService;

    @NonNull
    private final CpeRepository cpeRepository;

    @NonNull
    private final ProductClassGroupRepository productClassGroupRepository;

    @NonNull
    private final CpeMethodRepository cpeMethodRepository;

    @NonNull
    private final JwtService jwtService;
    @NonNull
    private final ParameterService parameterService;

    @NonNull
    private final TemplateService templateService;

    @NonNull
    private final DeviceSoftwareService softwareService;

    @NonNull
    private final CpeLogEventNameEntityRepository cpeLogEventNameEntityRepository;


    public DeviceTabsResponse getTabs(final String token, final Long deviceId) {
        Session session = jwtService.getSession(token);
        ClientType clientType = session.getClientType();
        Optional<Integer> protocolId = cpeRepository.getProtocolTypeByDevice(deviceId);

        final List<DeviceTab> tabs = TabUtil.getDeviceTabs(clientType)
                .stream()
                .filter(t -> t.getPath().equals("device-tabs"))
                .map(this::cloneTab)
                .collect(Collectors.toList());
        if (!parameterService.isAnyDiagnosticsExists(deviceId)) {
            removeTab(tabs, "Diagnostics");
        }

        ProtocolType protocolType = ProtocolType.TR069;

        if (protocolId.isPresent()) {
            protocolType = ProtocolType.fromValue(protocolId.get());
            if (Arrays.asList(ProtocolType.MQTT, ProtocolType.LWM2M, ProtocolType.USP).contains(protocolType)) {
                removeTab(tabs, "Custom RPC");
            }
        }

        if (!cpeMethodRepository.isCpeMethodExistsByCpeIdAndMethodName(deviceId, "Device.DeviceInfo.FirmwareImage.0.Download()") &&
                !cpeMethodRepository.isCpeMethodExistsByCpeIdAndMethodName(deviceId, "Download") &&
                !cpeMethodRepository.isCpeMethodExistsByCpeIdAndMethodName(deviceId, "Root.Firmware Update.0.Update") &&
                !(protocolType.equals(ProtocolType.USP) && parameterService.isValidUSPVersion(deviceId))) {
            removeFilesTab(tabs, "Download");
        }


        if (!cpeMethodRepository.isCpeMethodExistsByCpeIdAndMethodName(deviceId, "Device.DeviceInfo.FirmwareImage.0.Upload()") &&
                !cpeMethodRepository.isCpeMethodExistsByCpeIdAndMethodName(deviceId, "Upload") &&
                !(protocolType.equals(ProtocolType.USP) && parameterService.isValidUSPVersion(deviceId))) {
            removeFilesTab(tabs, "Upload");
        }

        if (clientType == ClientType.sc) {
            if (isSimplifiedViewNotEmpty(deviceId)) {
                final DeviceTab tabView = DeviceTab.builder()
                        .name("Basic view")
                        .path("basic-view")
                        .build();
                setByPath("device-settings", tabs, tabView);
            }
        }

        final List<DeviceTab> deviceViewTabs = getDeviceViewTabs(deviceId, clientType);
        if (deviceViewTabs != null && !deviceViewTabs.isEmpty()) {
            final DeviceTab tabView = DeviceTab.builder()
                    .name("Group view")
                    .path("group-view")
                    .items(deviceViewTabs)
                    .build();

            setByPath("device-settings", tabs, tabView);

            if (clientType == ClientType.mc) {
                deviceViewTabs.forEach(tab -> setByPath("group-view", tabs, tab));
            }
        }

        if(clientType == ClientType.mc) {
            final DeviceTab advancedViewTab = DeviceTab.builder()
                    .name("Advanced view")
                    .path("advanced-view")
                    .build();

            setByPath("device-settings", tabs, advancedViewTab);
        }

        if (!protocolType.equals(ProtocolType.MQTT) || !parameterService.isParamExistLike(deviceId, "Device.FriendlySmartHome.%")) {
            final List<DeviceTab> provisionTabs = provisionService.getProvisionTabs(protocolType);
            if (provisionTabs != null && !provisionTabs.isEmpty()) {
                final DeviceTab tabView = DeviceTab.builder()
                        .name("Provision manager")
                        .path("provision-manager")
                        .items(provisionTabs)
                        .build();
                setByPath("device-tabs", tabs, tabView);
            }
        }


        final List<DeviceTab> activityTabs = activityService.getActivityTabs(deviceId);
        if (activityTabs != null && !activityTabs.isEmpty()) {
            if (clientType == ClientType.mc) {
                activityTabs.forEach(tab -> setByPath("device-tabs", tabs, tab));
            } else {
                final DeviceTab tabView = DeviceTab.builder()
                        .name("Activity and logs")
                        .path("activity-and-logs")
                        .items(activityTabs)
                        .build();
                setByPath("device-tabs", tabs, tabView);
            }
        }

        DeviceTab softwareTab = softwareService.getSoftwareTab(deviceId);
        if (softwareTab != null) {
            setByPath("device-tabs", tabs, softwareTab);
        }

        if(protocolType.equals(ProtocolType.MQTT) || protocolType.equals(ProtocolType.LWM2M)) {
            removeTabByPath(tabs, "user-experience");
        }

        List<DeviceTab> deviceTabs = tabs.stream()
                .flatMap(tab -> tab.getItems().stream())
                .filter(t -> t.getItems() == null || !t.getItems().isEmpty())
                .sorted(Comparator.comparing(DeviceTab::getName, String.CASE_INSENSITIVE_ORDER))
                .map(this::sortsSubTubs)
                .collect(Collectors.toList());
        return new DeviceTabsResponse(deviceTabs);
    }

    public DeviceTabsResponse getProfileTabs(final String token, final String manufacturer, final String model) {
        Session session = jwtService.getSession(token);
        ClientType clientType = session.getClientType();
        Optional<ProductClassGroupEntity> opt = productClassGroupRepository.findFirstByManufacturerNameAndModelOrderById(manufacturer, model);
        if (!opt.isPresent()) {
            loggingProductClassGroupNotFound(manufacturer, model);
            return new DeviceTabsResponse(Collections.emptyList());
        }
        ProductClassGroupEntity productClassGroup = opt.get();

        final List<DeviceTab> tabs = TabUtil.getDeviceTabs(clientType)
                .stream()
                .filter(t -> t.getPath().equals("profile-tabs"))
                .map(this::cloneTab)
                .collect(Collectors.toList());
        ProtocolType protocolType = ProtocolType.fromValue(productClassGroup.getProtocolId() == null ? 0 : productClassGroup.getProtocolId());

        if (manufacturer.equalsIgnoreCase("default")) {
            removeTabByPath(tabs, "download");
        }

        if (!protocolType.equals(ProtocolType.TR069)) {
            removeTabByPath(tabs, "policy");
        }

        if (manufacturer.equalsIgnoreCase("default")) {
            final List<DeviceTab> deviceViewTabs = Customization.getProfileDefaultTabViewMap()
                    .values()
                    .stream()
                    .flatMap(map -> map.values().stream())
                    .map(e -> DeviceTab.builder()
                            .name("labels." + e.getName())
                            .path(e.getName())
                            .build())
                    .collect(Collectors.toList());
            if (deviceViewTabs != null && !deviceViewTabs.isEmpty()) {
                setItemsForPaths(Arrays.asList("parameters", "policy", "automation-parameters"), tabs.get(0).getItems(), deviceViewTabs);
            }
        } else {
            final List<DeviceTab> deviceViewTabs = getGroupViewTabs(productClassGroup.getId(), clientType);

            if (deviceViewTabs != null && !deviceViewTabs.isEmpty()) {
                setItemsForPaths(Arrays.asList("parameters", "policy", "automation-parameters"), tabs.get(0).getItems(), deviceViewTabs);
            }
        }

        return new DeviceTabsResponse(tabs);
    }

    private static void loggingProductClassGroupNotFound(String manufacturer, String model) {
        log.error("Manufacturer {} and model {} don't exist", manufacturer, model);
    }

    private DeviceTab sortsSubTubs(DeviceTab deviceTab) {
        if (deviceTab.getPath() != null && deviceTab.getItems() != null) {
            List<DeviceTab> collect = deviceTab.getItems().stream()
                    .sorted(Comparator.comparing(DeviceTab::getName, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
            deviceTab.setItems(collect);
        }
        return deviceTab;
    }

    private static void removeFilesTab(List<DeviceTab> tabs, String tabName) {
        for (DeviceTab tab : tabs.get(0).getItems()) {
            if (tab.getItems() != null) {
                if (!tab.getPath().equals("files")) {
                    continue;
                }
                tab.setItems(tab.getItems().stream()
                        .filter(t -> !t.getName().equals(tabName))
                        .collect(Collectors.toList()));
            }
        }
    }

    private static void removeTab(List<DeviceTab> tabs, String tabName) {
        for (DeviceTab tab : tabs) {
            if (tab.getItems() != null) {
                tab.setItems(tab.getItems().stream()
                        .filter(t -> !t.getName().equals(tabName))
                        .collect(Collectors.toList()));
            }
        }
    }

    private static void removeTabByPath(List<DeviceTab> tabs, String path) {
        for (DeviceTab tab : tabs) {
            if (tab.getItems() != null) {
                tab.setItems(tab.getItems().stream()
                        .filter(t -> !t.getPath().equals(path))
                        .collect(Collectors.toList()));
            }
        }
    }

    private DeviceTab cloneTab(DeviceTab tab) {
        if (tab == null || tab.getItems() == null) {
            return tab;
        }
        return tab.toBuilder()
                .items(tab.getItems()
                        .stream()
                        .map(this::cloneTab)
                        .collect(Collectors.toList()))
                .build();
    }

    private void setByPath(final String path, final List<DeviceTab> tabs, DeviceTab tab) {
        //todo(t): why don't the tabs set to items?
        if (tabs == null) {
            return;
        }
        tabs.forEach(t -> {
            if (t.getPath().equals(path)) {
                if (t.getItems() == null) {
                    t.setItems(new ArrayList<>());
                }
                if (!t.getItems().contains(tab)) {
                    t.getItems().add(tab);
                }
                return;
            }
            setByPath(path, t.getItems(), tab);
        });
    }

    private void setItemsByPath(final String path, final List<DeviceTab> tabs, List<DeviceTab> items) {
        if (tabs == null) {
            return;
        }
        tabs.forEach(t -> {
            if (t.getPath().equals(path)) {
                t.setItems(items);
            }
        });
    }

    private void setItemsForPaths(final List<String> paths, final List<DeviceTab> tabs, List<DeviceTab> items) {
        if (tabs == null) {
            return;
        }
        tabs.forEach(t -> {
            if (paths.contains(t.getPath())) {
                t.setItems(items);
            }
        });
    }

    private List<DeviceTab> getDeviceViewTabs(final Long deviceId, ClientType ct) {
        return getViewTabs(deviceId, parameterService, ct);
    }

    private List<DeviceTab> getGroupViewTabs(final Long groupId, ClientType ct) {
        return getViewTabs(groupId, templateService, ct);
    }

    private List<DeviceTab> getViewTabs(final Long ownerId, IParameterHelper parameterHelper, ClientType ct) {
        final Map<String, TreeTab> deviceSettingTabs =
                TabUtil.getDeviceTabForRoot(ct, parameterHelper.getRootParamName(ownerId));

        return deviceSettingTabs.entrySet()
                .stream()
                .filter(e -> TabUtil.getDeviceTabObjectsWithParameters(e.getValue()).stream()
                        .anyMatch(o -> parameterHelper.isParamExistLike(ownerId, o.replace(".i.", ".%.") + "%")))
                .map(e -> DeviceTab.builder()
                        .name("labels." + e.getKey())
                        .path(e.getKey())
                        .build())
                .map(this::setPriority)
                .sorted(Comparator.comparing(DeviceTab::getPriority))
                .collect(Collectors.toList());
    }

    private DeviceTab setPriority(DeviceTab tab) {
        switch (tab.getName()) {
            case "Management":
                tab.setPriority(1);
                break;
            case "Info":
                tab.setPriority(2);
                break;
            case "Time":
                tab.setPriority(3);
                break;
            case "Cellular":
                tab.setPriority(4);
                break;
            case "WiFi":
                tab.setPriority(5);
                break;
            case "IP":
                tab.setPriority(6);
                break;
            case "Firewall":
                tab.setPriority(7);
                break;
            case "DHCPv4":
                tab.setPriority(8);
                break;
            case "DHCPv6":
                tab.setPriority(9);
                break;
            case "DNS":
                tab.setPriority(10);
                break;
            case "Users":
                tab.setPriority(11);
                break;
            case "Ethernet":
                tab.setPriority(12);
                break;
            case "VoIP":
                tab.setPriority(13);
                break;
            default:
                break;
        }
        return tab;
    }

    private boolean isSimplifiedViewNotEmpty(final Long deviceId) {
        final List<DeviceSimplifiedParams> simplifiedParams =
                DeviceUtils.getDeviceSimplifiedParams(ClientType.sc, parameterService.getRootParamName(deviceId));

        if (simplifiedParams == null) {
            return false;
        }

        return simplifiedParams.stream()
                .anyMatch(s -> s.getItems()
                        .stream()
                        .anyMatch(i -> parameterService.isParamExist(deviceId, i.getFullName()))
                );
    }

    private boolean filterTab(final Long deviceId, final Map<String, Map<String, DeviceTabView>> deviceSettingTabs,
                              final String tab) {
        return deviceSettingTabs.get(tab).keySet()
                .stream()
                .filter(p -> !p.endsWith("."))
                .anyMatch(param -> parameterService.isParamExist(deviceId, param));
    }

    public DeviceObjectsSimpleResponse getDeviceTabView(final String token, final DeviceTabViewBody body) {
        Session session = jwtService.getSession(token);
        return getTabView(body.getDeviceId(), body.getTabPath(), parameterService, session.getClientType());
    }

    public DeviceObjectsSimpleResponse getManufAndModelTabView(String token, String manufacturer, String model, String tabPath) {
        Session session = jwtService.getSession(token);
        Optional<ProductClassGroupEntity> opt = productClassGroupRepository.findByManufacturerNameAndModel(manufacturer, model);
        if (!opt.isPresent()) {
            loggingProductClassGroupNotFound(manufacturer, model);
            return new DeviceObjectsSimpleResponse(Collections.emptyList());
        }
        ProductClassGroupEntity productClassGroup = opt.get();

        return getTabView(productClassGroup.getId(), tabPath, templateService, session.getClientType());
    }

    private DeviceObjectsSimpleResponse getTabView(Long ownerId, String tabPath, IParameterHelper parameterHelper, ClientType ct) {
        TreeTab treeTab = TabUtil.getDeviceTabForRootAndPath(parameterHelper.getRootParamName(ownerId), tabPath, ct);
        if (treeTab == null) {
            throw new FriendlyIllegalArgumentException(TAB_NOT_FOUND, tabPath);
        }
        List<String> tabParams = TabUtil.getDeviceTabObjectsUnderRoot(treeTab)
                .stream()
                .map(s -> parameterHelper.getParamNamesLike(ownerId, s.replace(".i.", ".%.") + "%"))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return new DeviceObjectsSimpleResponse(TabUtil.paramsToTabTree(treeTab, tabParams));
    }

    private DeviceObjectsSimpleResponse getProfileParameterTabView(Long ownerId, String tabPath, ClientType ct) {
        if (ownerId == 0L) {
            return new DeviceObjectsSimpleResponse(Customization.getProfileDefaultTabViewMap()
                    .values()
                    .stream()
                    .map(m -> {
                        TreeTab treeTab = m.get(tabPath);
                        if (treeTab != null) {
                            return DeviceObjectSimple.builder()
                                    .fullName(treeTab.getRoot().getFullName())
                                    .shortName(treeTab.getRoot().getShortName())
                                    .items(treeTab.getRoot().getItems().stream().map(o -> DeviceObjectSimple.builder()
                                            .fullName(o.getFullName())
                                            .shortName(ParameterUtil.getShortName(o.getFullName()))
                                            .parentName(o.getFullName().substring(0, o.getFullName().length() - 1))
                                            .parameters(o.getParameters().stream().map(p -> DeviceParameterSimple.builder()
                                                    .fullName(p.getFullName())
                                                    .shortName(ParameterUtil.getShortName(p.getFullName()))
                                                    .valueType(TabViewType.valueOf(p.getParameterValue().getValueType().name()))
                                                    .parentName(o.getFullName().substring(0, o.getFullName().length() - 1))
                                                    .build()).collect(Collectors.toList()))
                                            .build()).collect(Collectors.toList()))
                                    .parameters(treeTab.getRoot().getParameters().stream().map(p -> DeviceParameterSimple.builder()
                                            .fullName(p.getFullName())
                                            .shortName(ParameterUtil.getShortName(p.getFullName()))
                                            .valueType(TabViewType.valueOf(p.getParameterValue().getValueType().name()))
                                            .parentName(treeTab.getRoot().getFullName().substring(0, treeTab.getRoot().getFullName().length() - 1))
                                            .build()).collect(Collectors.toList()))
                                    .build();
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        } else {
            String rootName = templateService.getRootParamName(ownerId);
            Map<String, TreeTab> map = TabUtil.getProfileAllTabForRoot(rootName);
            TreeTab treeTab = map != null && map.containsKey(tabPath) ? map.get(tabPath) : TabUtil.getDeviceTabForRootAndPath(rootName, tabPath, ct);

            if (treeTab == null) {
                throw new FriendlyIllegalArgumentException(TAB_NOT_FOUND, tabPath);
            }
            List<String> tabParams = TabUtil.getDeviceTabObjectsUnderRoot(treeTab)
                    .stream()
                    .map(s -> templateService.getWriteParamNamesLike(ownerId, s.replace(".i.", ".%.") + "%"))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            return new DeviceObjectsSimpleResponse(TabUtil.paramsToTabTree(treeTab, tabParams));
        }
    }

    public DeviceObjectsSimpleResponse getProfileParameterManufAndModelTabViewWithTaskType(String token, String manufacturer, String model, TaskTypeTabView taskType, String tabPath) {
        Session session = jwtService.getSession(token);
        ProductClassGroupEntity productClassGroup = productClassGroupRepository.findByManufacturerNameAndModel(manufacturer, model).orElse(null);
        if (productClassGroup == null) {
            loggingProductClassGroupNotFound(manufacturer, model);
            return new DeviceObjectsSimpleResponse(Collections.emptyList());
        }
        if(taskType == null){
            return getProfileParameterManufAndModelTabView(session, manufacturer, model, tabPath);
        }
        else if(taskType.equals(GET_TASK) || taskType.equals(SET_ATTRIBUTES_TASK)){
            return getDefaultTabViewCanWriteableByTabPath(tabPath, session.getClientType(), productClassGroup);
        }
        else if(taskType.equals(SET_VALUE_TASK)){
            return getDefaultTabViewCannotWriteableByTabPath(tabPath, session.getClientType(), productClassGroup);
        }
        throw new FriendlyEntityNotFoundException(TAB_NOT_FOUND, tabPath);
    }

    private DeviceObjectsSimpleResponse getDefaultTabViewCanWriteableByTabPath(String tabPath, ClientType ct, ProductClassGroupEntity productClassGroup) {

        String rootName = templateService.getRootParamName(productClassGroup.getId());
        Map<String, TreeTab> map = TabUtil.getDeviceTabForRoot(ct, rootName);
        TreeTab treeTab = map != null && map.containsKey(tabPath) ? map.get(tabPath) : TabUtil.getDeviceTabForRootAndPath(rootName, tabPath, ct);

        if (treeTab == null) {
            throw new FriendlyIllegalArgumentException(TAB_NOT_FOUND, tabPath);
        }
        List<String> tabParams = TabUtil.getDeviceTabObjectsUnderRoot(treeTab)
                .stream()
                .map(s -> templateService.getParamNamesLike(productClassGroup.getId(), s.replace(".i.", ".%.") + "%"))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return new DeviceObjectsSimpleResponse(TabUtil.paramsToTabTree(treeTab, tabParams));
    }

    private DeviceObjectsSimpleResponse getDefaultTabViewCannotWriteableByTabPath(String tabPath, ClientType ct, ProductClassGroupEntity productClassGroup) {
        String rootName = templateService.getRootParamName(productClassGroup.getId());
        Map<String, TreeTab> map = TabUtil.getDeviceTabForRoot(ct, rootName);
        TreeTab treeTab = map != null && map.containsKey(tabPath) ? map.get(tabPath) : TabUtil.getDeviceTabForRootAndPath(rootName, tabPath, ct);

        if (treeTab == null) {
            throw new FriendlyIllegalArgumentException(TAB_NOT_FOUND, tabPath);
        }
        List<String> tabParams = TabUtil.getDeviceTabObjectsUnderRoot(treeTab)
                .stream()
                .map(s -> templateService.getWriteParamNamesLike(productClassGroup.getId(), s.replace(".i.", ".%.") + "%"))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        return new DeviceObjectsSimpleResponse(TabUtil.paramsToTabTree(treeTab, tabParams));
    }

    public DeviceObjectsSimpleResponse getProfileParameterManufAndModelTabView(Session session, String manufacturer, String model, String tabPath) {
        if (manufacturer.equalsIgnoreCase("default")) {
            return getProfileParameterTabView(0L, tabPath, session.getClientType());
        } else {
            Optional<ProductClassGroupEntity> opt = productClassGroupRepository.findByManufacturerNameAndModel(manufacturer, model);
            if (!opt.isPresent()) {
                loggingProductClassGroupNotFound(manufacturer, model);
                return new DeviceObjectsSimpleResponse(Collections.emptyList());
            }
            ProductClassGroupEntity productClassGroup = opt.get();

            return getProfileParameterTabView(productClassGroup.getId(), tabPath, session.getClientType());
        }
    }

    public DeviceProfileAutomationEventsNamesResponse getProfileEventsTabNamesByManufAndModel(String token, String manufacturer, String model) {
        Session session = jwtService.getSession(token);
        if (session.getClientType() == ClientType.mc) {
            ProductClassGroupEntity productClassGroup = productClassGroupRepository.findByManufacturerNameAndModel(manufacturer, model)
                    .orElseThrow(() -> new FriendlyEntityNotFoundException(PRODUCT_CLASS_GROUP_NOT_FOUND));
            Integer protocolId = productClassGroup.getProtocolId();
            if (protocolId == null) {
                return DeviceProfileAutomationEventsNamesResponse.builder()
                        .items(cpeLogEventNameEntityRepository.findAllByProtocolId(ProtocolType.TR069.getValue()))
                        .build();
            } else {
                return DeviceProfileAutomationEventsNamesResponse.builder()
                        .items(cpeLogEventNameEntityRepository.findAllByProtocolId(protocolId))
                        .build();
            }
        } else {
            throw new FriendlyIllegalArgumentException(CLIENT_TYPES_ARE_NOT_COMPATIBLE);
        }
    }

    public DeviceTabsResponse getProfileTabsByProductClassAndPath(String token, String manufacturer, String model, String tabPath) {
        DeviceTabsResponse deviceTabs = getProfileTabs(token, manufacturer, model);
        DeviceTab deviceTab = deviceTabs
                .getItems()
                .get(0)
                .getItems().stream()
                .filter(e -> e.getPath().equals(tabPath))
                .findFirst()
                .orElseThrow(() -> new FriendlyEntityNotFoundException(TAB_NOT_FOUND, tabPath));
        return new DeviceTabsResponse(deviceTab.getItems());
    }
}
