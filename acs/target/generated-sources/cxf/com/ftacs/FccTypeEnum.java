
package com.ftacs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fccTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="fccTypeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Speed"/&gt;
 *     &lt;enumeration value="Latency"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "fccTypeEnum")
@XmlEnum
public enum FccTypeEnum {

    @XmlEnumValue("Speed")
    SPEED("Speed"),
    @XmlEnumValue("Latency")
    LATENCY("Latency");
    private final String value;

    FccTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FccTypeEnum fromValue(String v) {
        for (FccTypeEnum c: FccTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
