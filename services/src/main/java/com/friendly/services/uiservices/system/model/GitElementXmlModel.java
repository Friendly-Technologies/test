package com.friendly.services.uiservices.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@XmlType(name = "git")
public class GitElementXmlModel {
    @XmlAttribute
    String branch;

    @XmlAttribute
    String revision;
}