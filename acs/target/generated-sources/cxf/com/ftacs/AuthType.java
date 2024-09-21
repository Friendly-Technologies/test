
package com.ftacs;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for authType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="authType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="NoAuth"/&gt;
 *     &lt;enumeration value="OAUTH2"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "authType")
@XmlEnum
public enum AuthType {

    @XmlEnumValue("NoAuth")
    NO_AUTH("NoAuth"),
    @XmlEnumValue("OAUTH2")
    OAUTH_2("OAUTH2");
    private final String value;

    AuthType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AuthType fromValue(String v) {
        for (AuthType c: AuthType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
