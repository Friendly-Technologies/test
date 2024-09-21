
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createOrUpdateCpeSubDevice complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createOrUpdateCpeSubDevice"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="serial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="mac" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cpeParamList" type="{http://ftacs.com/}cpeParamListWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createOrUpdateCpeSubDevice", propOrder = {
    "serial",
    "mac",
    "cpeParamList"
})
public class CreateOrUpdateCpeSubDevice {

    protected String serial;
    protected String mac;
    protected CpeParamListWS cpeParamList;

    /**
     * Gets the value of the serial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerial() {
        return serial;
    }

    /**
     * Sets the value of the serial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerial(String value) {
        this.serial = value;
    }

    /**
     * Gets the value of the mac property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMac() {
        return mac;
    }

    /**
     * Sets the value of the mac property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMac(String value) {
        this.mac = value;
    }

    /**
     * Gets the value of the cpeParamList property.
     * 
     * @return
     *     possible object is
     *     {@link CpeParamListWS }
     *     
     */
    public CpeParamListWS getCpeParamList() {
        return cpeParamList;
    }

    /**
     * Sets the value of the cpeParamList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpeParamListWS }
     *     
     */
    public void setCpeParamList(CpeParamListWS value) {
        this.cpeParamList = value;
    }

}
