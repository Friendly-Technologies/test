package com.friendly.services.device.info.utils;

import com.friendly.commons.models.device.DeviceActivity;
import com.friendly.services.device.info.model.TaskParam;
import com.friendly.services.uiservices.customization.Customization;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;

public class DeviceActivityUtil {

    private final static Map<String, String> DEVICE_ACTIVITY_SORT_MAP = new HashMap<>();
    private final static Map<String, Comparator<DeviceActivity>> DEVICE_ACTIVITY_COMPARATOR_ASC_MAP = new HashMap<>();
    private final static Map<String, Comparator<DeviceActivity>> DEVICE_ACTIVITY_COMPARATOR_DESC_MAP = new HashMap<>();
    private final static Map<Sort.Direction, Map<String, Comparator<DeviceActivity>>> DEVICE_ACTIVITY_COMPARATOR_MAP = new HashMap<>();
    private final static Map<String, Comparator<TaskParam>> TASK_PARAM_COMPARATOR_ASC_MAP = new HashMap<>();
    private final static Map<String, Comparator<TaskParam>> TASK_PARAM_COMPARATOR_DESC_MAP = new HashMap<>();
    private final static Map<Sort.Direction, Map<String, Comparator<TaskParam>>> TASK_PARAM_COMPARATOR_MAP = new HashMap<>();
    private final static List<String> INTERFACE_PARAMS = new ArrayList<>(Arrays.asList(
            "InternetGatewayDevice.WANDevice.%.WANConnectionDevice.%.WANPPPConnection.%.",
            "InternetGatewayDevice.LANDevice.%.LANHostConfigManagement.IPInterface.%.",
            "Device.IP.Interface.%.IPv%Address.%.",
            "Device.PPP.Interface.%.",
            "InternetGatewayDevice.WANDevice.%.WANConnectionDevice.%.WANIPConnection.%."));

    static {
        fillActivitySortMap();
        fillActivityCustSortMap();
    }

    public static String getDeviceActivitySort(final String field) {
        return DEVICE_ACTIVITY_SORT_MAP.getOrDefault(field, "id");
    }



    public static Comparator<DeviceActivity> getDeviceActivityComparator(final Sort.Direction direction,
                                                                         final String sortField) {
        return DEVICE_ACTIVITY_COMPARATOR_MAP
                .get(direction)
                .getOrDefault(sortField,
                        Comparator.nullsLast(
                                Comparator.comparing(DeviceActivity::getTaskId,
                                        Comparator.nullsLast(Comparator.reverseOrder()))));
    }

    private static void fillActivitySortMap() {
        DEVICE_ACTIVITY_SORT_MAP.put("taskId", "id");
        DEVICE_ACTIVITY_SORT_MAP.put("taskState", "state");
        DEVICE_ACTIVITY_SORT_MAP.put("taskName", "task_name");
        DEVICE_ACTIVITY_SORT_MAP.put("created", "created");
        DEVICE_ACTIVITY_SORT_MAP.put("completed", "completed");
        DEVICE_ACTIVITY_SORT_MAP.put("errorCode", "fault_code");
        DEVICE_ACTIVITY_SORT_MAP.put("errorText", "description");
        DEVICE_ACTIVITY_SORT_MAP.put("parameterName", "comparator.name");
        DEVICE_ACTIVITY_SORT_MAP.put("value", "comparator.value");
        DEVICE_ACTIVITY_SORT_MAP.put("creator", "comparator.creator");
        DEVICE_ACTIVITY_SORT_MAP.put("application", "comparator.application");
    }

    public static Comparator<TaskParam> getTaskParamComparator(final Sort.Direction direction,
                                                               final String sortField) {
        return TASK_PARAM_COMPARATOR_MAP.get(direction).getOrDefault(sortField, Comparator.nullsLast(
                Comparator.comparing(TaskParam::getName,
                        Comparator.nullsLast(Comparator.naturalOrder()))));
    }

    public static List<String> getInterfaceParams() {
        return INTERFACE_PARAMS;
    }

    private static void fillActivityCustSortMap() {
        final Comparator<DeviceActivity> nameComparator = (d1, d2) -> {
            final String leftFirstTask = isParameterNull(d1)
                    || d1.getParameters().get(0).getFullName() == null
                    ? null : d1.getParameters().get(0).getFullName();
            final String rightFirstTask = isParameterNull(d2)
                    || d2.getParameters().get(0).getFullName() == null
                    ? null : d2.getParameters().get(0).getFullName();
            return compare(leftFirstTask, rightFirstTask);
        };

        final Comparator<DeviceActivity> valueComparator = (d1, d2) -> {
            final String leftFirstTask = isParameterNull(d1)
                    || d1.getParameters().get(0).getValue() == null
                    ? null : (String) d1.getParameters().get(0).getValue();
            final String rightFirstTask = isParameterNull(d2)
                    || d2.getParameters().get(0).getValue() == null
                    ? null : (String) d2.getParameters().get(0).getValue();
            return compare(leftFirstTask, rightFirstTask);
        };
        DEVICE_ACTIVITY_COMPARATOR_ASC_MAP.put("comparator.name", nameComparator);
        DEVICE_ACTIVITY_COMPARATOR_ASC_MAP.put("comparator.value", valueComparator);
        DEVICE_ACTIVITY_COMPARATOR_ASC_MAP.put("comparator.creator", Comparator.nullsLast(
                Comparator.comparing(DeviceActivity::getCreator,
                        Comparator.nullsLast(Comparator.naturalOrder()))));
        DEVICE_ACTIVITY_COMPARATOR_ASC_MAP.put("comparator.application", Comparator.nullsLast(
                Comparator.comparing(DeviceActivity::getApplication,
                        Comparator.nullsLast(Comparator.naturalOrder()))));

        DEVICE_ACTIVITY_COMPARATOR_DESC_MAP.put("comparator.name", nameComparator.reversed());
        DEVICE_ACTIVITY_COMPARATOR_DESC_MAP.put("comparator.value", valueComparator.reversed());
        DEVICE_ACTIVITY_COMPARATOR_DESC_MAP.put("comparator.creator", Comparator.nullsLast(
                Comparator.comparing(DeviceActivity::getCreator,
                        Comparator.nullsLast(Comparator.reverseOrder()))));
        DEVICE_ACTIVITY_COMPARATOR_DESC_MAP.put("comparator.application", Comparator.nullsLast(
                Comparator.comparing(DeviceActivity::getApplication,
                        Comparator.nullsLast(Comparator.reverseOrder()))));

        TASK_PARAM_COMPARATOR_ASC_MAP.put("comparator.name", Comparator.nullsLast(
                Comparator.comparing(TaskParam::getName,
                        Comparator.nullsLast(Comparator.naturalOrder()))));
        TASK_PARAM_COMPARATOR_ASC_MAP.put("comparator.value", Comparator.nullsLast(
                Comparator.comparing(TaskParam::getValue,
                        Comparator.nullsLast(Comparator.naturalOrder()))));
        TASK_PARAM_COMPARATOR_DESC_MAP.put("comparator.name", Comparator.nullsLast(
                Comparator.comparing(TaskParam::getName,
                        Comparator.nullsLast(Comparator.reverseOrder()))));
        TASK_PARAM_COMPARATOR_DESC_MAP.put("comparator.value", Comparator.nullsLast(
                Comparator.comparing(TaskParam::getValue,
                        Comparator.nullsLast(Comparator.reverseOrder()))));

        DEVICE_ACTIVITY_COMPARATOR_MAP.put(Sort.Direction.ASC, DEVICE_ACTIVITY_COMPARATOR_ASC_MAP);
        DEVICE_ACTIVITY_COMPARATOR_MAP.put(Sort.Direction.DESC, DEVICE_ACTIVITY_COMPARATOR_DESC_MAP);
        TASK_PARAM_COMPARATOR_MAP.put(Sort.Direction.ASC, TASK_PARAM_COMPARATOR_ASC_MAP);
        TASK_PARAM_COMPARATOR_MAP.put(Sort.Direction.DESC, TASK_PARAM_COMPARATOR_DESC_MAP);
    }

    public static String getDeviceActivityName(String s) {
        s = fixTaskName(s);
        return Customization.getDefaultDeviceActivity().getOrDefault(s, s);
    }

    public static Set<String> getDeviceActivityKey(String taskName) {
        HashSet<String> keys = new HashSet<>();
        if (hasNoSpaces(taskName)) {
            keys.add(taskName + "%");
            return keys;
        }
        return getKeysByValueWithoutSubstrings(Customization.getDefaultDeviceActivity(), taskName)
                .stream()
                .map(string -> "%" + string + "%")
                .collect(Collectors.toSet());

    }

    public static <E> Set<String> getKeysByValueWithoutSubstrings(Map<String, E> map, E value) {
        Set<String> keys = new HashSet<>();
        boolean copy;
        for (Map.Entry<String, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                if (keys.isEmpty()) {
                    keys.add(entry.getKey());
                } else {
                    copy = false;
                    for (String elem : keys) {
                        if (elem.contains(entry.getKey()) || entry.getKey().contains(elem)) {
                            copy = true;
                            break;
                        }
                    }
                    if (!copy) {
                        keys.add(entry.getKey());
                    }
                }
            }
        }
        return keys;
    }

    public static boolean hasNoSpaces(String s) {
        return !s.contains(" ");
    }

    public static String fixTaskName(String taskName) {
        String prefix = "Composite: ";
        if (taskName.startsWith(prefix)) {
            taskName = taskName.substring(prefix.length());
        }

        String regex = "[0-9-]+[a-zA-Z]+[-\\w|]*";
        while (taskName.matches(regex)) {
            taskName = taskName.substring(taskName.indexOf("-") + 1);
        }

        return taskName;
    }

    private static boolean isParameterNull(final DeviceActivity deviceUpdate) {
        return deviceUpdate.getParameters() == null
                || deviceUpdate.getParameters().isEmpty();
    }

    private static int compare(String leftFirstTask, String rightFirstTask) {
        if (leftFirstTask == null && rightFirstTask == null) {
            return 0;
        } else if (leftFirstTask == null) {
            return -1;
        } else if (rightFirstTask == null) {
            return 1;
        } else {
            return leftFirstTask.compareTo(rightFirstTask);
        }
    }
}