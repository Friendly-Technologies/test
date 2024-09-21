package com.friendly.services.uiservices.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@XmlType(name = "version")
@XmlRootElement(name = "version")
public class VersionXmlModel {
    @XmlAttribute
    String version;

    @XmlElement
    BuildElementXmlModel build;

    @XmlElement
    GitElementXmlModel git;
}