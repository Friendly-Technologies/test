package com.friendly.services.device.info.utils;

import com.friendly.services.device.info.orm.acs.model.DeviceEntity;
import com.friendly.services.device.info.utils.helper.QueryViewHelper;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum FieldPath {
    DEVICE_ID("deviceId", root -> root.get("id")),
    DOMAIN_ID("domainId", root -> root.get("domainId")),
    CREATED("created", root -> root.get("created")),
    UPDATED("updated", root -> root.get("updated")),
    STATUS("status", root -> root.get("isOnline")),
    SERIAL("serial", root -> root.get("serial")),
    FIRMWARE("firmware", root -> root.get("firmware")),
    PROTOCOL_TYPE("protocolType", root -> root.get("protocolId")),
    DOMAIN_NAME("domainName", root -> root.get("domainName")),
    MANUFACTURER("manufacturer", root -> root.get("productClass").get("productGroup").get("manufacturerName")),
    MODEL("model", root -> root.get("productClass").get("productGroup").get("model")),
    OUI("oui", root -> root.get("productClass").get("manufacturer").get("oui")),
    USER_LOGIN("userLogin", root -> root.get("customDevice").get("userLogin")),
    USER_NAME("userName", root -> root.get("customDevice").get("userName")),
    PHONE("phone", root -> root.get("customDevice").get("phone")),
    ZIP("zip", root -> root.get("customDevice").get("zip")),
    USER_LOCATION("userLocation", root -> root.get("customDevice").get("userLocation")),
    USER_TAG("userTag", root -> root.get("customDevice").get("userTag")),
    USER_STATUS("userStatus", root -> root.get("customDevice").get("userStatus")),
    USER_ID("userId", root -> root.get("customDevice").get("userId")),
    CUST1("cust1", root -> root.get("customDevice").get("cust1")),
    CUST2("cust2", root -> root.get("customDevice").get("cust2")),
    CUST3("cust3", root -> root.get("customDevice").get("cust3")),
    CUST4("cust4", root -> root.get("customDevice").get("cust4")),
    CUST5("cust5", root -> root.get("customDevice").get("cust5")),
    CUST6("cust6", root -> root.get("customDevice").get("cust6")),
    CUST7("cust7", root -> root.get("customDevice").get("cust7")),
    CUST8("cust8", root -> root.get("customDevice").get("cust8")),
    CUST9("cust9", root -> root.get("customDevice").get("cust9")),
    CUST10("cust10", root -> root.get("customDevice").get("cust10")),
    CUST11("cust11", root -> root.get("customDevice").get("cust11")),
    CUST12("cust12", root -> root.get("customDevice").get("cust12")),
    CUST13("cust13", root -> root.get("customDevice").get("cust13")),
    CUST14("cust14", root -> root.get("customDevice").get("cust14")),
    CUST15("cust15", root -> root.get("customDevice").get("cust15")),
    CUST16("cust16", root -> root.get("customDevice").get("cust16")),
    CUST17("cust17", root -> root.get("customDevice").get("cust17")),
    CUST18("cust18", root -> root.get("customDevice").get("cust18")),
    CUST19("cust19", root -> root.get("customDevice").get("cust19")),
    CUST20("cust20", root -> root.get("customDevice").get("cust20")),
    LATITUDE("latitude", root -> root.get("customDevice").get("latitude")),
    LONGITUDE("longitude", root -> root.get("customDevice").get("longitude")),
    PARAMETER_VALUE("parameterValue", root -> QueryViewHelper.resolveJoin(root).get("value")),
    PARAMETER_NAME("parameterName", root -> QueryViewHelper.resolveJoin(root).get("nameId"));

    private final String fieldName;
    private final Function<Root<DeviceEntity>, Path<Object>> pathFunction;

    FieldPath(String fieldName, Function<Root<DeviceEntity>, Path<Object>> pathFunction) {
        this.fieldName = fieldName;
        this.pathFunction = pathFunction;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Path<Object> getPath(Root<DeviceEntity> root) {
        return pathFunction.apply(root);
    }

    private static final Map<String, Function<Root<DeviceEntity>, Path<Object>>> FIELD_PATH_MAP = new HashMap<>();

    static {
        for (FieldPath field : FieldPath.values()) {
            FIELD_PATH_MAP.put(field.getFieldName(), field::getPath);
        }
    }

    public static Map<String, Function<Root<DeviceEntity>, Path<Object>>> getFieldPathMap() {
        return Collections.unmodifiableMap(FIELD_PATH_MAP);
    }
}

