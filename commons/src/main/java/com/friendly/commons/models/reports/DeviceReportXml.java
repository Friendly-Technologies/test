package com.friendly.commons.models.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "devices")
public class DeviceReportXml implements XmlFile {

    @XmlElement(name="device")
    private List<DeviceXml> devices;

}
