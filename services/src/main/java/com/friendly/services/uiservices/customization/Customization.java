package com.friendly.services.uiservices.customization;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.AppPorts;
import com.friendly.commons.models.device.DeviceConfig;
import com.friendly.commons.models.device.DeviceConfigType;
import com.friendly.commons.models.tabs.DeviceTab;
import com.friendly.commons.models.device.rpc.RpcMethod;
import com.friendly.commons.models.device.setting.DeviceSimplifiedParams;
import com.friendly.commons.models.device.tools.ReplaceService;
import com.friendly.commons.models.tree.TreeTab;

import java.util.*;
import java.util.function.Supplier;

public class Customization {
    private final List<Long> DEFAULT_MONITORING_LIST = new ArrayList<>();
    private final Map<String, List<RpcMethod>> RCP_METHODS_MAP = new HashMap<>();
    private final Map<String, List<DeviceSimplifiedParams>> SIMPLIFIED_VIEW_MAP = new HashMap<>();
    private final List<AppPorts> APP_PORTS = new ArrayList<>();
    private final List<ReplaceService> REPLACE_SERVICES = new ArrayList<>();
    private final List<DeviceTab> DEVICE_TABS = new ArrayList<>();
    private final Map<String, Map<String, TreeTab>> DEVICE_TAB_VIEW_MAP = new HashMap<>();
    private final Map<DeviceConfigType, List<DeviceConfig>> DEVICE_CONFIG = new EnumMap<>(DeviceConfigType.class);
    private final Map<String, List<String>> CUSTOM_PARAMS = new HashMap<>();
    private final Map<String, String> DEVICE_ACTIVITY_MAP = new HashMap<>();
    private final Map<String, Map<String, TreeTab>> PROFILE_DEFAULT_TAB_VIEW_MAP = new HashMap<>();
    private final Map<String, Map<String, TreeTab>> PROFILE_ALL_TAB_VIEW_MAP = new HashMap<>();

    private static Map<ClientType, Customization> data = new HashMap<>();

    public List<Long> getDefaultMonitoringList() {
        return DEFAULT_MONITORING_LIST;
    }

    public List<AppPorts> getAppPorts() {
        return APP_PORTS;
    }
    public List<ReplaceService> getReplaceServices() {
        return REPLACE_SERVICES;
    }
    public List<DeviceTab> getDeviceTabs() {
        return DEVICE_TABS;
    }
    public Map<String, Map<String, TreeTab>> getProfileDefaultTabs() {
        return PROFILE_DEFAULT_TAB_VIEW_MAP;
    }
    public Map<String, Map<String, TreeTab>> getProfileAllTabs() {
        return PROFILE_ALL_TAB_VIEW_MAP;
    }

    public Map<String, List<RpcMethod>> getRpcMethodsMap() {
        return RCP_METHODS_MAP;
    }

    public Map<String, List<DeviceSimplifiedParams>> getSimplifiedViewMap() {
        return SIMPLIFIED_VIEW_MAP;
    }

    public Map<String, Map<String, TreeTab>> getDeviceTabViewMap() {
        return DEVICE_TAB_VIEW_MAP;
    }

    public Map<DeviceConfigType, List<DeviceConfig>> getDeviceConfig() {
        return DEVICE_CONFIG;
    }

    public Map<String, List<String>> getCustomParams() {
        return CUSTOM_PARAMS;
    }

    public Map<String, String> getDeviceActivity() {
        return DEVICE_ACTIVITY_MAP;
    }

    public void fillDefaultMonitoringList(List<Long> src) {
        DEFAULT_MONITORING_LIST.clear();
        DEFAULT_MONITORING_LIST.addAll(src);
    }

    public void fillAppPorts(List<AppPorts> src) {
        APP_PORTS.clear();
        APP_PORTS.addAll(src);
    }
    public void fillReplaceServices(List<ReplaceService> src) {
        REPLACE_SERVICES.clear();
        REPLACE_SERVICES.addAll(src);
    }
    public void fillDeviceTabs(List<DeviceTab> src) {
        DEVICE_TABS.clear();
        DEVICE_TABS.addAll(src);
    }

    public void fillRpcMethodsMap(Map<String, List<RpcMethod>> src) {
        RCP_METHODS_MAP.clear();
        RCP_METHODS_MAP.putAll(src);
    }

    public void fillSimplifiedViewMap(Map<String, List<DeviceSimplifiedParams>> src) {
        SIMPLIFIED_VIEW_MAP.clear();
        SIMPLIFIED_VIEW_MAP.putAll(src);
    }

    public void fillDeviceTabViewMap(Map<String, Map<String, TreeTab>> src) {
        DEVICE_TAB_VIEW_MAP.clear();
        DEVICE_TAB_VIEW_MAP.putAll(src);
    }

    public void fillDeviceConfig(Map<DeviceConfigType, List<DeviceConfig>> src) {
        DEVICE_CONFIG.clear();
        DEVICE_CONFIG.putAll(src);
    }

    public void fillCustomParams(Map<String, List<String>> src) {
        CUSTOM_PARAMS.clear();
        CUSTOM_PARAMS.putAll(src);
    }

    public void fillDeviceActivity(Map<String, String> src) {
        DEVICE_ACTIVITY_MAP.clear();
        DEVICE_ACTIVITY_MAP.putAll(src);
    }

    public void fillProfileDefaultTabs(Map<String, Map<String, TreeTab>> src) {
        PROFILE_DEFAULT_TAB_VIEW_MAP.clear();
        PROFILE_DEFAULT_TAB_VIEW_MAP.putAll(src);
    }

    public void fillProfileAllTabs(Map<String, Map<String, TreeTab>> src) {
        PROFILE_ALL_TAB_VIEW_MAP.clear();
        PROFILE_ALL_TAB_VIEW_MAP.putAll(src);
    }

    public static List<Long> getDefaultMonitoringListForClient(ClientType ct) {
        return getListForClientOrDefault(data.get(ct)::getDefaultMonitoringList, data.get(ClientType.def)::getDefaultMonitoringList);
    }

    public static List<AppPorts> getAppPortsForClient(ClientType ct) {
        return getListForClientOrDefault(data.get(ct)::getAppPorts, data.get(ClientType.def)::getAppPorts);
    }

    public static List<ReplaceService> getReplaceServicesForClient(ClientType ct) {
        return getListForClientOrDefault(data.get(ct)::getReplaceServices, data.get(ClientType.def)::getReplaceServices);
    }

    public static List<DeviceTab> getDeviceTabsForClient(ClientType ct) {
        return getListForClientOrDefault(data.get(ct)::getDeviceTabs, data.get(ClientType.def)::getDeviceTabs);
    }

    public static Map<String, List<RpcMethod>> getRpcMethodsMapForClient(ClientType ct) {
        return getMapForClientOrDefault(data.get(ct)::getRpcMethodsMap, data.get(ClientType.def)::getRpcMethodsMap);
    }

    public static Map<String, List<DeviceSimplifiedParams>> getSimplifiedViewMapForClient(ClientType ct) {
        return getMapForClientOrDefault(data.get(ct)::getSimplifiedViewMap, data.get(ClientType.def)::getSimplifiedViewMap);
    }

    public static Map<String, Map<String, TreeTab>> getDeviceTabViewMapForClient(ClientType ct) {
        return getMapForClientOrDefault(data.get(ct)::getDeviceTabViewMap, data.get(ClientType.def)::getDeviceTabViewMap);
    }

    public static Map<DeviceConfigType, List<DeviceConfig>> getDeviceConfigForClient(ClientType ct) {
        return getMapForClientOrDefault(data.get(ct)::getDeviceConfig, data.get(ClientType.def)::getDeviceConfig);
    }

    public static Map<String, List<String>> getCustomParamsForClient(ClientType ct) {
        return getMapForClientOrDefault(data.get(ct)::getCustomParams, data.get(ClientType.def)::getCustomParams);
    }

    public static Map<String, String> getDeviceActivityForClient(ClientType ct) {
        return getMapForClientOrDefault(data.get(ct)::getDeviceActivity, data.get(ClientType.def)::getDeviceActivity);
    }

    public static List<Long> getDefaultDefaultMonitoringList() {
        return data.get(ClientType.def).getDefaultMonitoringList();
    }

    public static List<AppPorts> getDefaultAppPorts() {
        return data.get(ClientType.def).getAppPorts();
    }

    public static List<ReplaceService> getDefaultReplaceServices() {
        return data.get(ClientType.def).getReplaceServices();
    }

    public static List<DeviceTab> getDefaultDeviceTabs() {
        return data.get(ClientType.def).getDeviceTabs();
    }

    public static Map<String, List<RpcMethod>> getDefaultRpcMethodsMap() {
        return data.get(ClientType.def).getRpcMethodsMap();
    }

    public static Map<String, List<DeviceSimplifiedParams>> getDefaultSimplifiedViewMap() {
        return data.get(ClientType.def).getSimplifiedViewMap();
    }

    public static Map<String, Map<String, TreeTab>> getDefaultDeviceTabViewMap() {
        return data.get(ClientType.def).getDeviceTabViewMap();
    }

    public static Map<String, Map<String, TreeTab>> getProfileDefaultTabViewMap() {
        return data.get(ClientType.mc).getProfileDefaultTabs();
    }

    public static Map<String, Map<String, TreeTab>> getProfileAllTabViewMap() {
        return data.get(ClientType.mc).getProfileAllTabs();
    }

    public static Map<DeviceConfigType, List<DeviceConfig>> getDefaultDeviceConfig() {
        return data.get(ClientType.def).getDeviceConfig();
    }

    public static Map<String, List<String>> getDefaultCustomParams(final ClientType clientType) {
        return data.get(clientType).getCustomParams();
    }

    public static Map<String, String> getDefaultDeviceActivity() {
        return data.get(ClientType.def).getDeviceActivity();
    }

    public static void fillDefaultMonitoringListForClient(ClientType ct, List<Long> src) {
        data.computeIfAbsent(ct, clientType -> new Customization()).fillDefaultMonitoringList(src);
    }

    public static void fillAppPortsForClient(ClientType ct, List<AppPorts> src) {
        data.computeIfAbsent(ct, clientType -> new Customization()).fillAppPorts(src);
    }
    public static void fillReplaceServicesForClient(ClientType ct, List<ReplaceService> src) {
        data.computeIfAbsent(ct, clientType -> new Customization()).fillReplaceServices(src);
    }
    public static void fillDeviceTabsForClient(ClientType ct, List<DeviceTab> src) {
        data.computeIfAbsent(ct, clientType -> new Customization()).fillDeviceTabs(src);
    }

    public static void fillRpcMethodsMapForClient(ClientType ct, Map<String, List<RpcMethod>> src) {
        data.computeIfAbsent(ct, clientType -> new Customization()).fillRpcMethodsMap(src);
    }

    public static void fillSimplifiedViewMapForClient(ClientType ct, Map<String, List<DeviceSimplifiedParams>> src) {
        data.computeIfAbsent(ct, clientType -> new Customization()).fillSimplifiedViewMap(src);
    }

    public static void fillDeviceTabViewMapForClient(ClientType ct, Map<String, Map<String, TreeTab>> src) {
        data.computeIfAbsent(ct, clientType -> new Customization()).fillDeviceTabViewMap(src);
    }

    public static void fillDeviceConfigForClient(ClientType ct, Map<DeviceConfigType, List<DeviceConfig>> src) {
        data.computeIfAbsent(ct, clientType -> new Customization()).fillDeviceConfig(src);
    }

    public static void fillCustomParamsForClient(ClientType ct, Map<String, List<String>> src) {
        data.computeIfAbsent(ct, clientType -> new Customization()).fillCustomParams(src);
    }

    public static void fillDeviceActivityForClient(ClientType ct, Map<String, String> src) {
        data.computeIfAbsent(ct, clientType -> new Customization()).fillDeviceActivity(src);
    }

    public static void fillProfileDefaultTabViewMap(Map<String, Map<String, TreeTab>> src) {
        data.computeIfAbsent(ClientType.mc, clientType -> new Customization()).fillProfileDefaultTabs(src);
    }

    public static void fillProfileAllTabViewMap(Map<String, Map<String, TreeTab>> src) {
        data.computeIfAbsent(ClientType.mc, clientType -> new Customization()).fillProfileAllTabs(src);
    }

    private static <E> List<E> getListForClientOrDefault(Supplier<List<E>> func, Supplier<List<E>> defaultFunc) {
        List<E> list = func.get();
        if (list.isEmpty()) {
            list = defaultFunc.get();
        }
        return list;
    }

    private static <K, V> Map<K, V> getMapForClientOrDefault(Supplier<Map<K, V>> func, Supplier<Map<K, V>> defaultFunc) {
        Map<K, V> map = func.get();
        if (map.isEmpty()) {
            map = defaultFunc.get();
        }
        return map;
    }
}