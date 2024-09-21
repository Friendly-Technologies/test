package com.friendly.services.device.info.utils;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.DeviceColumns;
import com.friendly.commons.models.device.DeviceDisplayType;
import com.friendly.commons.models.view.Condition;
import com.friendly.commons.models.view.ConditionInputType;
import com.friendly.commons.models.view.ConditionItem;
import com.friendly.commons.models.view.ConditionType;
import com.friendly.commons.models.view.FilterType;
import com.friendly.commons.models.view.ViewCondition;
import com.friendly.commons.models.view.ViewType;
import com.friendly.services.device.info.utils.helper.QueryViewHelper;
import com.friendly.services.management.profiles.ConditionGroupType;
import com.friendly.services.uiservices.customization.Customization;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeParameterEntity;
import com.friendly.services.device.parameterstree.orm.acs.model.CpeParameterNameEntity;
import com.friendly.services.device.info.orm.acs.model.DeviceEntity;
import com.friendly.services.uiservices.view.orm.iotw.model.ColumnConditionEntity;
import com.friendly.services.uiservices.view.orm.iotw.model.ConditionItemEntity;
import com.friendly.services.uiservices.frame.orm.iotw.model.FrameConditionEntity;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.friendly.commons.models.view.ConditionInputType.DATE;
import static com.friendly.commons.models.view.ConditionInputType.DATETIME;
import static com.friendly.commons.models.view.ConditionInputType.MANUFACTURER_NAME;
import static com.friendly.commons.models.view.ConditionInputType.MODEL_NAME;
import static com.friendly.commons.models.view.ConditionInputType.NUMBER;
import static com.friendly.commons.models.view.ConditionInputType.TEXT;
import static com.friendly.commons.models.view.ConditionInputType.TEXT_ARRAY;
import static com.friendly.commons.models.view.ConditionType.BeforeToday;
import static com.friendly.commons.models.view.ConditionType.Equal;
import static com.friendly.commons.models.view.ConditionType.InHierarchy;
import static com.friendly.commons.models.view.ConditionType.InList;
import static com.friendly.commons.models.view.ConditionType.IsNotNull;
import static com.friendly.commons.models.view.ConditionType.IsNull;
import static com.friendly.commons.models.view.ConditionType.LaterThan;
import static com.friendly.commons.models.view.ConditionType.Like;
import static com.friendly.commons.models.view.ConditionType.NotEqual;
import static com.friendly.commons.models.view.ConditionType.NotLike;
import static com.friendly.commons.models.view.ConditionType.Off;
import static com.friendly.commons.models.view.ConditionType.On;
import static com.friendly.commons.models.view.ConditionType.OnDay;
import static com.friendly.commons.models.view.ConditionType.Prev7Days;
import static com.friendly.commons.models.view.ConditionType.PrevXDays;
import static com.friendly.commons.models.view.ConditionType.PriorTo;
import static com.friendly.commons.models.view.ConditionType.Regexp;
import static com.friendly.commons.models.view.ConditionType.StartsWith;
import static com.friendly.commons.models.view.ConditionType.Today;
import static com.friendly.commons.models.view.ConditionType.Yesterday;
import static com.friendly.commons.models.view.FilterType.Coordinate;
import static com.friendly.commons.models.view.FilterType.Date;
import static com.friendly.commons.models.view.FilterType.Domain;
import static com.friendly.commons.models.view.FilterType.IpAddress;
import static com.friendly.commons.models.view.FilterType.MacAddress;
import static com.friendly.commons.models.view.FilterType.Manufacturer;
import static com.friendly.commons.models.view.FilterType.Model;
import static com.friendly.commons.models.view.FilterType.OnOrOff;
import static com.friendly.commons.models.view.FilterType.Protocol;
import static com.friendly.commons.models.view.FilterType.Serial;
import static com.friendly.commons.models.view.FilterType.Tasks;
import static com.friendly.commons.models.view.FilterType.TextValue;
import static com.friendly.commons.models.view.ViewType.DeviceView;
import static com.friendly.commons.models.view.ViewType.FrameView;
import static com.friendly.commons.models.view.ViewType.GroupUpdateView;
import static com.friendly.commons.models.view.ViewType.SearchView;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.COLUMN_KEY_NOT_FOUND;
import static com.friendly.services.device.info.utils.ColumnId.USER_ID;
import static com.friendly.services.device.info.utils.ColumnId.USER_LOCATION;
import static com.friendly.services.device.info.utils.ColumnId.USER_STATUS;
import static com.friendly.services.device.info.utils.ColumnId.USER_TAG;
import static com.friendly.services.device.info.utils.ColumnId.ZIP;
import static java.lang.Boolean.FALSE;

@Slf4j
public class DeviceViewUtil {

    private DeviceViewUtil () {
        throw new IllegalStateException("Utility class");
    }

    private static final Map<String, Map<ColumnId, String>> COLUMN_NAME_LANG_MAP = new HashMap<>();
    private static final Map<ConditionType, String> CONDITION_NAME_MAP = new EnumMap<>(ConditionType.class);
    private static final Map<FilterType, List<Condition>> FILTER_TYPE_MAP = new LinkedHashMap<>();
    private static final List<DeviceColumns> GROUP_CONDITION_FILTER_COLUMNS = new ArrayList<>();
    private static final Map<String, List<DeviceColumns>> GROUP_CONDITION_INFORM_FILTER_COLUMNS = new HashMap<>();
    private static final Map<ConditionGroupType, List<Condition>> GROUP_CONDITION_COMPARISONS =
            new EnumMap<>(ConditionGroupType.class);
    private static final Map<String, Map<ConditionType, String>> CONDITION_NAME_LANG_MAP = new HashMap<>();
    private static final List<String> CUSTOM_PARAMS_MAC_ADDRESS = new ArrayList<>();
    private static final Set<String> CUSTOM_DEVICE_PARAMS = new HashSet<>();

    public static final Map<String, List<String>> PARAMETER_NAMES_SEARCH = new HashMap<>();
    public static final Map<String, List<Long>> PARAMETER_NAME_SEARCH_IDS = new HashMap<>();

    static {
        init();
    }

    public static void init() {
        COLUMN_NAME_LANG_MAP.put("EN", ColumnName.getColumnNameMap());
        fillConditionNameMap();
        fillFilterTypeMap();
        fillGroupConditionFilterColumns();
        fillGroupConditionInformFilterColumns();
        fillGroupConditionComparisons();
    }

    private static void fillConditionNameMap() {
        CONDITION_NAME_MAP.put(Equal, "=");
        CONDITION_NAME_MAP.put(NotEqual, "!=");
        CONDITION_NAME_MAP.put(Like, "Like");
        CONDITION_NAME_MAP.put(NotLike, "Not like");
        CONDITION_NAME_MAP.put(StartsWith, "Starts with");
        CONDITION_NAME_MAP.put(InList, "In list");
        CONDITION_NAME_MAP.put(IsNull, "Is null");
        CONDITION_NAME_MAP.put(IsNotNull, "Is not null");
        CONDITION_NAME_MAP.put(OnDay, "On day");
        CONDITION_NAME_MAP.put(PriorTo, "Prior to");
        CONDITION_NAME_MAP.put(LaterThan, "Later than");
        CONDITION_NAME_MAP.put(Today, "Today");
        CONDITION_NAME_MAP.put(BeforeToday, "Before today");
        CONDITION_NAME_MAP.put(Yesterday, "Yesterday");
        CONDITION_NAME_MAP.put(Prev7Days, "Prev 7 days");
        CONDITION_NAME_MAP.put(PrevXDays, "Prev X days");
        CONDITION_NAME_MAP.put(On, "Online");
        CONDITION_NAME_MAP.put(Off, "Offline");
        CONDITION_NAME_MAP.put(Regexp, "Regexp");
        CONDITION_NAME_MAP.put(InHierarchy, "In hierarchy");
        CONDITION_NAME_LANG_MAP.put("EN", CONDITION_NAME_MAP);
    }

    private static void fillFilterTypeMap() {
        FILTER_TYPE_MAP.put(
                Coordinate,
                Arrays.asList(
                        new Condition(Equal, CONDITION_NAME_MAP.get(Equal), NUMBER),
                        new Condition(NotEqual, CONDITION_NAME_MAP.get(NotEqual), NUMBER),
                        new Condition(Like, CONDITION_NAME_MAP.get(Like), NUMBER),
                        new Condition(NotLike, CONDITION_NAME_MAP.get(NotLike), NUMBER),
                        new Condition(IsNull, CONDITION_NAME_MAP.get(IsNull), null),
                        new Condition(IsNotNull, CONDITION_NAME_MAP.get(IsNotNull), null)));
        FILTER_TYPE_MAP.put(
                TextValue,
                Arrays.asList(
                        new Condition(Equal, CONDITION_NAME_MAP.get(Equal), TEXT),
                        new Condition(NotEqual, CONDITION_NAME_MAP.get(NotEqual), TEXT),
                        new Condition(Like, CONDITION_NAME_MAP.get(Like), TEXT),
                        new Condition(NotLike, CONDITION_NAME_MAP.get(NotLike), TEXT),
                        new Condition(StartsWith, CONDITION_NAME_MAP.get(StartsWith), TEXT),
                        new Condition(InList, CONDITION_NAME_MAP.get(InList), TEXT_ARRAY),
                        new Condition(IsNull, CONDITION_NAME_MAP.get(IsNull), null),
                        new Condition(IsNotNull, CONDITION_NAME_MAP.get(IsNotNull), null)));
        FILTER_TYPE_MAP.put(Tasks, new ArrayList<>());
        FILTER_TYPE_MAP.put(
                Date,
                Arrays.asList(
                        new Condition(IsNull, CONDITION_NAME_MAP.get(IsNull), null),
                        new Condition(IsNotNull, CONDITION_NAME_MAP.get(IsNotNull), null),
                        new Condition(OnDay, CONDITION_NAME_MAP.get(OnDay), DATE),
                        new Condition(PriorTo, CONDITION_NAME_MAP.get(PriorTo), DATETIME),
                        new Condition(LaterThan, CONDITION_NAME_MAP.get(LaterThan), DATETIME),
                        new Condition(Today, CONDITION_NAME_MAP.get(Today), null),
                        new Condition(BeforeToday, CONDITION_NAME_MAP.get(BeforeToday), null),
                        new Condition(Yesterday, CONDITION_NAME_MAP.get(Yesterday), null),
                        new Condition(Prev7Days, CONDITION_NAME_MAP.get(Prev7Days), null),
                        new Condition(PrevXDays, CONDITION_NAME_MAP.get(PrevXDays), NUMBER)));
        FILTER_TYPE_MAP.put(
                Manufacturer,
                Arrays.asList(
                        new Condition(Equal, CONDITION_NAME_MAP.get(Equal), MANUFACTURER_NAME),
                        new Condition(NotEqual, CONDITION_NAME_MAP.get(NotEqual), MANUFACTURER_NAME)));
        FILTER_TYPE_MAP.put(
                Model,
                Arrays.asList(
                        new Condition(Equal, CONDITION_NAME_MAP.get(Equal), MODEL_NAME),
                        new Condition(NotEqual, CONDITION_NAME_MAP.get(NotEqual), MODEL_NAME)));
        FILTER_TYPE_MAP.put(
                Protocol,
                Arrays.asList(
                        new Condition(Equal, CONDITION_NAME_MAP.get(Equal), ConditionInputType.PROTOCOL_TYPE),
                        new Condition(NotEqual, CONDITION_NAME_MAP.get(NotEqual), ConditionInputType.PROTOCOL_TYPE)));
        FILTER_TYPE_MAP.put(
                OnOrOff,
                Arrays.asList(
                        new Condition(On, CONDITION_NAME_MAP.get(On), null),
                        new Condition(Off, CONDITION_NAME_MAP.get(Off), null)));
        FILTER_TYPE_MAP.put(
                Domain,
                Arrays.asList(
                        new Condition(Equal, CONDITION_NAME_MAP.get(Equal), ConditionInputType.DOMAIN_ID),
                        new Condition(NotEqual, CONDITION_NAME_MAP.get(NotEqual), ConditionInputType.DOMAIN_ID),
                        new Condition(InHierarchy, CONDITION_NAME_MAP.get(InHierarchy), ConditionInputType.DOMAIN_ID),
                        new Condition(IsNull, CONDITION_NAME_MAP.get(IsNull), null)));
        FILTER_TYPE_MAP.put(
                Serial,
                Arrays.asList(
                        new Condition(Equal, CONDITION_NAME_MAP.get(Equal), TEXT),
                        new Condition(NotEqual, CONDITION_NAME_MAP.get(NotEqual), TEXT),
                        new Condition(Like, CONDITION_NAME_MAP.get(Like), TEXT),
                        new Condition(NotLike, CONDITION_NAME_MAP.get(NotLike), TEXT),
                        new Condition(StartsWith, CONDITION_NAME_MAP.get(StartsWith), TEXT),
                        new Condition(IsNull, CONDITION_NAME_MAP.get(IsNull), null),
                        new Condition(IsNotNull, CONDITION_NAME_MAP.get(IsNotNull), null)));
        FILTER_TYPE_MAP.put(
                MacAddress,
                Arrays.asList(
                        new Condition(Equal, CONDITION_NAME_MAP.get(Equal), TEXT),
                        new Condition(NotEqual, CONDITION_NAME_MAP.get(NotEqual), TEXT),
                        new Condition(Like, CONDITION_NAME_MAP.get(Like), TEXT),
                        new Condition(NotLike, CONDITION_NAME_MAP.get(NotLike), TEXT),
                        new Condition(StartsWith, CONDITION_NAME_MAP.get(StartsWith), TEXT),
                        new Condition(IsNull, CONDITION_NAME_MAP.get(IsNull), null),
                        new Condition(IsNotNull, CONDITION_NAME_MAP.get(IsNotNull), null)));
        FILTER_TYPE_MAP.put(
                IpAddress,
                Arrays.asList(
                        new Condition(Equal, CONDITION_NAME_MAP.get(Equal), TEXT),
                        new Condition(NotEqual, CONDITION_NAME_MAP.get(NotEqual), TEXT),
                        new Condition(Like, CONDITION_NAME_MAP.get(Like), TEXT),
                        new Condition(NotLike, CONDITION_NAME_MAP.get(NotLike), TEXT),
                        new Condition(StartsWith, CONDITION_NAME_MAP.get(StartsWith), TEXT),
                        new Condition(IsNull, CONDITION_NAME_MAP.get(IsNull), null),
                        new Condition(IsNotNull, CONDITION_NAME_MAP.get(IsNotNull), null)));
    }

    private static void fillGroupConditionFilterColumns() {
        GROUP_CONDITION_FILTER_COLUMNS.addAll(
                Arrays.asList(
                        new DeviceColumns(ZIP.getId(), true, true, true),
                        new DeviceColumns(USER_LOCATION.getId(), true, true, true),
                        new DeviceColumns(USER_TAG.getId(), true, true, true),
                        new DeviceColumns(USER_STATUS.getId(), true, true, true),
                        new DeviceColumns(USER_ID.getId(), true, true, true)));
    }

    private static void fillGroupConditionInformFilterColumns() {
        GROUP_CONDITION_INFORM_FILTER_COLUMNS.put(
                "Device.",
                Arrays.asList(
                        new DeviceColumns("Device.DeviceSummary", true, true, true),
                        new DeviceColumns("Device.DeviceInfo.HardwareVersion", true, true, true),
                        new DeviceColumns("Device.DeviceInfo.SoftwareVersion", true, true, true)));

        GROUP_CONDITION_INFORM_FILTER_COLUMNS.put(
                "InternetGatewayDevice.",
                Arrays.asList(
                        new DeviceColumns("InternetGatewayDevice.DeviceSummary", true, true, true),
                        new DeviceColumns("InternetGatewayDevice.DeviceInfo.SpecVersion", true, true, true),
                        new DeviceColumns("InternetGatewayDevice.DeviceInfo.HardwareVersion", true, true, true),
                        new DeviceColumns("InternetGatewayDevice.DeviceInfo.SoftwareVersion", true, true, true),
                        new DeviceColumns(
                                "InternetGatewayDevice." +
                                        "DeviceInfo.ProvisioningCode", true, true, true)));
    }

    private static void fillGroupConditionComparisons() {
        GROUP_CONDITION_COMPARISONS.put(
                ConditionGroupType.UserInfo,
                Arrays.asList(
                        new Condition(Equal, CONDITION_NAME_MAP.get(Equal), TEXT),
                        new Condition(NotEqual, CONDITION_NAME_MAP.get(NotEqual), TEXT),
                        new Condition(Regexp, CONDITION_NAME_MAP.get(Regexp), TEXT),
                        new Condition(IsNull, CONDITION_NAME_MAP.get(IsNull), null),
                        new Condition(IsNotNull, CONDITION_NAME_MAP.get(IsNotNull), null)));
        GROUP_CONDITION_COMPARISONS.put(
                ConditionGroupType.Inform,
                Arrays.asList(
                        new Condition(Equal, CONDITION_NAME_MAP.get(Equal), TEXT),
                        new Condition(NotEqual, CONDITION_NAME_MAP.get(NotEqual), TEXT),
                        new Condition(Regexp, CONDITION_NAME_MAP.get(Regexp), TEXT)));
    }

    public static List<DeviceColumns> getGroupConditionFilterColumns() {
        return GROUP_CONDITION_FILTER_COLUMNS;
    }

    public static List<DeviceColumns> getGroupConditionInformFilterColumns(String root) {
        return GROUP_CONDITION_INFORM_FILTER_COLUMNS.get(root);
    }

    public static List<Condition> getGroupConditionComparisonsByType(ConditionGroupType type) {
      return GROUP_CONDITION_COMPARISONS.get(type);
    }

    public static List<Condition> getFiltersByColumn(final String columnKey) {
        try {
            ColumnId columnId = ColumnId.getColumnIdMap().get(columnKey);
            FilterType filterType = ColumnFilter.getColumnFilterMap().get(columnId);
            return FILTER_TYPE_MAP.getOrDefault(filterType, Collections.emptyList());
        } catch (Exception e) {
            throw new FriendlyIllegalArgumentException(COLUMN_KEY_NOT_FOUND, columnKey);
        }
    }

    public static ViewCondition conditionEntityToCondition(final ColumnConditionEntity conditionEntity,
                                                           final String localeId, final String zoneId,
                                                           final String dateFormat, final String timeFormat) {
        return ViewCondition.builder()
                .id(conditionEntity.getId())
                // .parentId(conditionEntity.getParentId())
                .columnKey(conditionEntity.getColumnKey())
                .columnName(getColumnName(conditionEntity.getColumnKey(), localeId))
                .logic(conditionEntity.getLogic())
                .compare(conditionEntity.getType())
                .compareName(CONDITION_NAME_LANG_MAP.get(localeId).get(conditionEntity.getType()))
                .conditionString(conditionEntity.getStringValue())
                .conditionDateIso(conditionEntity.getDateValue())
                .conditionDate(
                        DateTimeUtils.format(conditionEntity.getDateValue(), zoneId, dateFormat, timeFormat))
                .build();
    }

    public static Predicate getPredicateFromCondition(final Root<DeviceEntity> root, final CriteriaBuilder cb,
                                                      final ColumnConditionEntity condition,
                                                      final ClientType clientType, CriteriaQuery<?> cq,
                                                      final String zoneId) {
        Predicate result;
        if (isParameterSearch(condition.getColumnKey())) {
            // It is search on cpe_parameter.value field condition
            // preparing subquery like (select id from cpe_parameter where cpe_id=[MAIN_Q_CPE_ID] and
            // name_id in ([parameterNameSearchIds]) and value [CONDITION]
            Subquery<CpeParameterEntity> sq = cq.subquery(CpeParameterEntity.class);
            Root<CpeParameterEntity> rootCpeParam = sq.from(CpeParameterEntity.class);

            List<Predicate> predicates = new ArrayList<>();
            // cpe_id=[MAIN_Q_CPE_ID]
            predicates.add(cb.equal(rootCpeParam.get("cpeId"), root.get("id")));

            List<String> names = DeviceViewUtil.PARAMETER_NAMES_SEARCH.get(condition.getColumnKey());
            names =
                    names == null
                            ? Collections.emptyList()
                            : names.stream().filter(n -> n.contains("%")).collect(Collectors.toList());

            // name_id in ([parameterNameSearchIds])
            Predicate namePred = null;
            List<Long> nameIds = PARAMETER_NAME_SEARCH_IDS.get(condition.getColumnKey());
            if (nameIds != null && !nameIds.isEmpty()) {
                namePred = rootCpeParam.get("nameId").in(PARAMETER_NAME_SEARCH_IDS.get(condition.getColumnKey()));
            }


            if (!names.isEmpty()) {
                Subquery<CpeParameterNameEntity> sqName = cq.subquery(CpeParameterNameEntity.class);
                Root<CpeParameterNameEntity> rootCpeParamName = sqName.from(CpeParameterNameEntity.class);
                Predicate[] arr =
                        names.stream()
                                .map(n -> cb.like(rootCpeParam.get("parameterName").get("name"), n))
                                .toArray(Predicate[]::new);
                sqName.select(rootCpeParamName.get("id")).where(cb.or(arr));

                namePred = namePred == null ? rootCpeParam.get("nameId").in(sqName)
                        : cb.or(namePred, rootCpeParam.get("nameId").in(sqName));
            }
            predicates.add(namePred);
            // value [CONDITION]
            predicates.add(
                    QueryViewHelper.getPredicateFromCondition(
                            rootCpeParam.get("value"), cb, condition, clientType, zoneId, root, cq));

            // preparing exists or not exists (for NotEqual/NotLike/IsNull conditions) query using
            // subquery
            sq = sq.select(rootCpeParam.get("id")).where(predicates.toArray(new Predicate[0]));

            result =
                    condition.getType().equals(NotEqual)
                            || condition.getType().equals(NotLike)
                            || condition.getType().equals(IsNull)
                            ? cb.not(cb.exists(sq))
                            : cb.exists(sq);
            // main query looks like this: select .... from CPE where ...[conditions on Device entity
            // fields].. AND [not] exists(...cpe_parameter subquery...,)
        } else {
            result = QueryViewHelper.getPredicateFromCondition(
                            FieldPath.getFieldPathMap().get(condition.getColumnKey()).apply(root),
                            cb,
                            condition,
                            clientType,
                            zoneId, root, cq);
        }
        return result;
    }

    public static String getConditionForType(String localeId, ConditionType type) {
        return CONDITION_NAME_LANG_MAP.get(localeId).get(type);
    }

    public static Predicate getPredicateFromSearchParam(final Root<DeviceEntity> root, final CriteriaBuilder cb,
                                                        final String searchColumn, final String searchParam,
                                                        final Boolean searchExact) {
        if (isParameterSearch(searchColumn)) {
            return cb.and(
                    getPredicatesForParameterSearch(root, cb, searchColumn, searchParam, searchExact));
        }
        if (searchExact == null || searchExact == FALSE) {
            return cb.like(
                    FieldPath.getFieldPathMap().get(searchColumn).apply(root).as(String.class), "%" + searchParam + "%");
        }
        return cb.equal(FieldPath.getFieldPathMap().get(searchColumn).apply(root), searchParam);
    }

    public static Predicate[] getPredicatesForParameterSearch(final Root<DeviceEntity> root, final CriteriaBuilder cb,
                                                              final String searchColumn, final String searchParam,
                                                              final Boolean searchExact) {
        return new Predicate[]{
                getPredicateForParameterNames(root, cb, searchColumn),
                cb.and(getPredicateFromSearchParam(root, cb, "parameterValue", searchParam, searchExact))
        };
    }

    public static Predicate getPredicateForParameterNames(
            final Root<DeviceEntity> root, final CriteriaBuilder cb, final String searchColumn) {

        return cb.and(FieldPath.getFieldPathMap()
                .get("parameterName")
                .apply(root)
                .in(PARAMETER_NAME_SEARCH_IDS.get(searchColumn)));
    }

    public static String getColumnName(final String columnKey, final String localeId) {
        ColumnId id = ColumnId.getColumnIdMap().get(columnKey);
        return COLUMN_NAME_LANG_MAP.get(localeId).get(id);
    }

    public static List<String> getCustParams(final String columnKey, final ClientType clientType) {
        return Customization.getDefaultCustomParams(clientType).get(columnKey);
    }

    public static List<String> getMacAddress() {
        return CUSTOM_PARAMS_MAC_ADDRESS;
    }

    public static boolean isParameterSearch(String searchColumn) {
        return PARAMETER_NAMES_SEARCH.containsKey(searchColumn);
    }

    public static boolean isCustomDeviceParam(String columnKey) {
        return CUSTOM_DEVICE_PARAMS.contains(columnKey);
    }

    public static void fillParameterMapping(Map<String, List<String>> map) {
        PARAMETER_NAMES_SEARCH.put(
            "ipAddress",
            Arrays.asList(
                "%ExternalIPAddress",
                "%.RemoteIPAddress",
                "Device.IP.Interface.%.IPv%Address.%.IPAddress",
                "%ConnectionRequestURL",
                "%UDPConnectionRequestAddress",
                "Device.LAN.IPAddress",
                "Root.Connectivity Monitoring.%.Router IP Addresses.%",
                "Root.Connectivity Monitoring.%.IP Addresses.%"));
        PARAMETER_NAMES_SEARCH.put("macAddress", Collections.singletonList("%MACAddress"));
        PARAMETER_NAMES_SEARCH.put("nodeBID", Collections.singletonList("%eNodeBID"));
        PARAMETER_NAMES_SEARCH.put("BSID", Collections.singletonList("%BSID"));
        PARAMETER_NAMES_SEARCH.put(
            "acsUsername",
            Arrays.asList(
                "InternetGatewayDevice.ManagementServer.Username",
                "Device.ManagementServer.Username"));
        PARAMETER_NAMES_SEARCH.put(
            "hardware",
            Arrays.asList(
                "InternetGatewayDevice.DeviceInfo.HardwareVersion",
                "Device.DeviceInfo.HardwareVersion"));
        PARAMETER_NAMES_SEARCH.put(
            "uptime",
            Arrays.asList("InternetGatewayDevice.DeviceInfo.UpTime", "Device.DeviceInfo.UpTime"));
        PARAMETER_NAMES_SEARCH.put(
            "software",
            Arrays.asList(
                "InternetGatewayDevice.DeviceInfo.SoftwareVersion",
                "Device.DeviceInfo.SoftwareVersion"));

        PARAMETER_NAMES_SEARCH.putAll(
            map.entrySet().stream()
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        e ->
                            e.getValue().stream()
                                .map(v -> v.replace(".i.", ".%."))
                                .collect(Collectors.toList()))));

        CUSTOM_DEVICE_PARAMS.add("userTag");
        CUSTOM_DEVICE_PARAMS.add("userLogin");
        CUSTOM_DEVICE_PARAMS.add("userName");
        CUSTOM_DEVICE_PARAMS.add("userId");
        CUSTOM_DEVICE_PARAMS.add("userStatus");
        CUSTOM_DEVICE_PARAMS.add("userLocation");
        CUSTOM_DEVICE_PARAMS.add("latitude");
        CUSTOM_DEVICE_PARAMS.add("longitude");
        CUSTOM_DEVICE_PARAMS.add("phone");
        CUSTOM_DEVICE_PARAMS.add("zip");
        for (int i = 1; i <= 20; i++) {
            CUSTOM_DEVICE_PARAMS.add("cust" + i);
        }
    }

    public static void fillCustomParamsMacAddress() {
        CUSTOM_PARAMS_MAC_ADDRESS.addAll(
                Customization.getDefaultCustomParams(ClientType.sc).get("macAddress").stream()
                        .map(p -> ("^" + p + "$").replaceAll("\\.i\\.", ".[0-9]."))
                        .collect(Collectors.toList()));
    }

    public static ViewCondition conditionFrameEntityToViewCondition(final FrameConditionEntity condition,
                                                                  final String locale, final String zoneId,
                                                                  final String dateFormat, final String timeFormat) {
      return ViewCondition.builder()
              .id(condition.getId())
              .columnKey(condition.getColumnKey())
              .columnName(getColumnName(condition.getColumnKey(), locale))
              .logic(condition.getLogic())
              .compare(condition.getCompare())
              .compareName(CONDITION_NAME_LANG_MAP.get(locale).get(condition.getCompare()))
              .conditionString(condition.getStringValue())
              .conditionDateIso(condition.getDateValue())
              .conditionDate(
                      DateTimeUtils.format(condition.getDateValue(), zoneId, dateFormat, timeFormat))
              .build();
    }

    public static ConditionItemEntity conditionItemToEntity(final ConditionItem conditionItem) {
        return ConditionItemEntity.builder().viewId(conditionItem.getViewId()).viewIndex(conditionItem.getViewIndex())
                .build();
    }

    public static ViewType getViewType(DeviceDisplayType displayType) {
        switch (displayType) {
            case LIST:
                return DeviceView;
            case SEARCH:
                return SearchView;
            case GROUP_UPDATE:
                return GroupUpdateView;
            default:
                return FrameView;
        }
    }
}
