
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for configurationParameter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="configurationParameter"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="groupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="protocolId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="defaultLocalizedGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="defaultLocalizedGroupDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="defaultLocalizedPropertyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="defaultLocalizedPropertyDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="locationId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "configurationParameter", propOrder = {
    "id",
    "name",
    "value",
    "groupName",
    "protocolId",
    "type",
    "defaultLocalizedGroupName",
    "defaultLocalizedGroupDescription",
    "defaultLocalizedPropertyName",
    "defaultLocalizedPropertyDescription",
    "locationId"
})
public class ConfigurationParameter {

    protected Long id;
    protected String name;
    protected String value;
    protected String groupName;
    protected Integer protocolId;
    protected String type;
    protected String defaultLocalizedGroupName;
    protected String defaultLocalizedGroupDescription;
    protected String defaultLocalizedPropertyName;
    protected String defaultLocalizedPropertyDescription;
    protected Integer locationId;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the groupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets the value of the groupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupName(String value) {
        this.groupName = value;
    }

    /**
     * Gets the value of the protocolId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getProtocolId() {
        return protocolId;
    }

    /**
     * Sets the value of the protocolId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setProtocolId(Integer value) {
        this.protocolId = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the defaultLocalizedGroupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultLocalizedGroupName() {
        return defaultLocalizedGroupName;
    }

    /**
     * Sets the value of the defaultLocalizedGroupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultLocalizedGroupName(String value) {
        this.defaultLocalizedGroupName = value;
    }

    /**
     * Gets the value of the defaultLocalizedGroupDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultLocalizedGroupDescription() {
        return defaultLocalizedGroupDescription;
    }

    /**
     * Sets the value of the defaultLocalizedGroupDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultLocalizedGroupDescription(String value) {
        this.defaultLocalizedGroupDescription = value;
    }

    /**
     * Gets the value of the defaultLocalizedPropertyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultLocalizedPropertyName() {
        return defaultLocalizedPropertyName;
    }

    /**
     * Sets the value of the defaultLocalizedPropertyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultLocalizedPropertyName(String value) {
        this.defaultLocalizedPropertyName = value;
    }

    /**
     * Gets the value of the defaultLocalizedPropertyDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultLocalizedPropertyDescription() {
        return defaultLocalizedPropertyDescription;
    }

    /**
     * Sets the value of the defaultLocalizedPropertyDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultLocalizedPropertyDescription(String value) {
        this.defaultLocalizedPropertyDescription = value;
    }

    /**
     * Gets the value of the locationId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLocationId() {
        return locationId;
    }

    /**
     * Sets the value of the locationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLocationId(Integer value) {
        this.locationId = value;
    }

}
