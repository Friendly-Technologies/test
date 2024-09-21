package com.friendly.services.settings.acs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@XmlType(name = "license")
@XmlRootElement
public class License implements Serializable {

    @XmlElement(name="uniqueId")
    private String uniqueId;

    @XmlElement(name="CPEAdminUsers")
    private String cpeAdminUsers;

    @XmlElement(name="CSRUsers")
    private String csrUsers;

    @XmlElement(name="CustomerName")
    private String customerName;

    @XmlElement(name="TimeExpiration")
    private String timeExpiration;

    @XmlElement(name="RegisteredCPE")
    private String registeredCpe;

    @XmlElement(name="RegisteredCPETR069")
    private String registeredCpeTR069;

    @XmlElement(name="RegisteredCPEUSP")
    private String registeredCpeUSP;

    @XmlElement(name="RegisteredCPELWM2M")
    private String registeredCpeLWM2M;

    @XmlElement(name="RegisteredCPEMQTT")
    private String registeredCpeMQTT;

    @XmlElement(name="ManagedCPE")
    private String managedCpe;

    @XmlElement(name="ManagedCPETR069")
    private String managedCpeTR069;

    @XmlElement(name="ManagedCPEUSP")
    private String managedCpeUSP;

    @XmlElement(name="ManagedCPELWM2M")
    private String managedCpeLWM2M;

    @XmlElement(name="ManagedCPEMQTT")
    private String managedCpeMQTT;

    @XmlElement(name="StartTime")
    private String startTime;

    @XmlElement(name="DayCount")
    private String dayCount;
}
