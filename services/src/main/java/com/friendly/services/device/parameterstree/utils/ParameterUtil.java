package com.friendly.services.device.parameterstree.utils;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.response.ParameterName;
import com.friendly.services.uiservices.customization.Customization;
import com.friendly.services.device.info.utils.DeviceViewUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class ParameterUtil {
    public static Comparator<String> comparatorParameterNames =
            Comparator.comparing((String o) -> o.replaceAll("\\d", ""), String.CASE_INSENSITIVE_ORDER)
                    .thenComparingInt(String::length)
                    .thenComparingInt(o -> {
                        String[] parts = o.split("\\D+");
                        return parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                    });

    public static boolean isNoIp(String ip) {
        if (StringUtils.isBlank(ip))
            return true;
        if (!ip.contains(".") && !ip.contains(":"))
            return true;
        return ip.equals("0.0.0.0") || ip.equals("::");
    }

    public static boolean isLocalIp(String ip) {
        if ((ip.startsWith("10.") || ip.startsWith("172.16.") || ip.startsWith("192.168.") || ip.equals("127.0.0.1"))) {
            return true;
        }
        ip = ip.toUpperCase();
        return ip.startsWith("FE") ||
                ip.startsWith("FD00:") ||
                ip.startsWith("FC00:") ||
                ip.equals("::") || ip.equals("::1");
    }

    public static String clobToString(final Clob data) {
        try {
            final StringBuilder sb = new StringBuilder();
            final Reader reader = data.getCharacterStream();

            final BufferedReader br = new BufferedReader(reader);
            String line;
            while (null != (line = br.readLine())) {
                sb.append(line).append("\n");
            }
            br.close();
            return sb.toString();
        } catch (SQLException | IOException e) {
            //ignore
        }
        return null;
    }

    public static String getShortName(final String fullName) {
        final String str = StringUtils.chop(fullName);
        final int index = str.lastIndexOf(".");
        if (index != -1) {
            if (index == fullName.length() - 1) {
                return fullName.substring(0, index + 1);
            } else {
                final String temp = fullName.substring(index + 1);
                return NumberUtils.isCreatable(temp) ? getShortName(fullName.substring(0, index)) + "." + temp : temp;
            }
        } else {
            return fullName;
        }
    }

    public static String getShortName(String input, boolean lastDot) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        int lastDotIndex = input.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return input;
        }

        if (lastDot) {
            return input.substring(lastDotIndex + 1);
        }

        int secondLastDotIndex = input.lastIndexOf('.', lastDotIndex - 1);
        if (secondLastDotIndex == -1) {
            return input;
        }

        return input.substring(secondLastDotIndex + 1, input.length() - 1);
    }

    public static boolean isValueValidForReference(String value) {
        return value != null && value.length() > 11 && value.contains(".") && (value.startsWith("Device")
                || value.startsWith("InternetGatewayDevice"));
    }

    public static String getType(final String type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case "TIME":
            case "dateTime":
                return "dateTime";
            case "unsignedLong":
            case "unsignedInt":
            case "long":
            case "INTEGER":
            case "int":
            case "FLOAT":
                return "int";
            case "boolean":
                return "boolean";
            case "Resource Instance":
                return "array";
            default:
                return "string";
        }
    }

    public static String getCustParamsMap(String paramName, Map<String, String> nameValueMap, ClientType clientType) {
        if (nameValueMap == null) {
            return "";
        }
        List<String> paramNames = DeviceViewUtil.getCustParams(paramName, clientType);
        return nameValueMap.entrySet().stream()
                .filter(p -> paramNames.contains(p.getKey()))
                .map(Map.Entry::getValue)
                .distinct()
                .collect(Collectors.joining(", "));
    }

    public static String getIpAddress(Map<String, String> nameValueMap, ClientType clientType) {
        if (nameValueMap == null) {
            return "";
        }
        List<java.util.function.Predicate<String>> predicates = Customization.getDefaultCustomParams(clientType).get("ipAddress")
                .stream()
                .map(p -> ("^" + p + "$").replaceAll("\\.i\\.", ".[0-9]."))
                .map(Pattern::compile)
                .map(Pattern::asPredicate)
                .collect(Collectors.toList());

        return nameValueMap.entrySet().stream()
                .filter(p -> isParamNameValid(p.getKey(), predicates))
                .map(Map.Entry::getValue)
                .distinct()
                .filter(n -> !StringUtils.isBlank(n))
                .collect(Collectors.joining(", "));

//        if (nameValueMap.containsKey(nameUdp) && !StringUtils.isEmpty(nameValueMap.get(nameUdp))) {
//            return ParameterUtil.getIpFromUrl(nameValueMap.get(nameUdp));
//        } else if (nameValueMap.containsKey(nameHttp) && !StringUtils.isEmpty(nameValueMap.get(nameHttp))) {
//            return ParameterUtil.getIpFromUrl(nameValueMap.get(nameHttp));
//        }
//        return "";
    }


    public static String getMacAddress(String activeConn, Map<String, String> nameValueMap) {
        List<java.util.function.Predicate<String>> predicates = DeviceViewUtil.getMacAddress().stream()
                .map(Pattern::compile)
                .map(Pattern::asPredicate)
                .collect(Collectors.toList());

        return nameValueMap.entrySet().stream()
                .filter(p -> isParamNameValid(p.getKey(), predicates))
                .filter(p -> activeConn == null || p.getKey().startsWith(activeConn))
                .map(Map.Entry::getValue)
                .distinct()
                .filter(n -> !StringUtils.isBlank(n))
                .collect(Collectors.joining(", "));
    }

    private static boolean isParamNameValid(String param, List<java.util.function.Predicate<String>> predicates) {
        return predicates.stream().anyMatch(predicate -> predicate.test(param));
    }

    public static String getNode(Map<String, String> nameValueMap) {
        if (nameValueMap == null || nameValueMap.isEmpty()) {
            return "";
        }
        if (nameValueMap.keySet().stream()
                .anyMatch(n -> n.matches("^InternetGatewayDevice.*$"))) {
            return "InternetGatewayDevice.";
        } else if (nameValueMap.keySet().stream()
                .anyMatch(n -> n.matches("^Device.*$"))) {
            return "Device.";
        } else {
            return "Root.";
        }
    }

    private static String getIpFromUrl(String url) {
        if (url.contains("http://")) {
            url = url.replaceAll("http://", "");
            return url.substring(0, url.indexOf(":"));
        }
        if (url.contains("tcp://")) {
            url = url.replaceAll("tcp://", "");
            return url.substring(0, url.indexOf(":"));
        }
        return url;
    }

    public static String removeShortName(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        int lastDotIndex = input.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return input.substring(0, lastDotIndex + 1);
        } else {
            return input;
        }
    }

    public static ParameterName makeProperName(ParameterName param) {
        String name = param.getShortName();

        if (name.endsWith(".")) {
            param.setShortName(name.substring(0, name.length() - 1));
            return makeProperName(param);
        }


        if (name.lastIndexOf(".") != -1) {
            param.setShortName(name.substring(name.lastIndexOf(".") + 1));
        }

        return param;
    }

    public static String maskIfNeeded(String name, String value) {
        return name.contains("KeyPassphrase") || name.contains("Password") ? "*****" : value;
    }
}