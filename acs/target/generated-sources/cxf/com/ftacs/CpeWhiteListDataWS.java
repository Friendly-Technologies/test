
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cpeWhiteListDataWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cpeWhiteListDataWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ipRange" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="manufacturer" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="model" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="onlyCreated" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="serial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cpeWhiteListDataWS", propOrder = {
    "ipRange",
    "manufacturer",
    "model",
    "onlyCreated",
    "serial"
})
public class CpeWhiteListDataWS {

    @XmlElement(required = true)
    protected String ipRange;
    @XmlElement(required = true)
    protected String manufacturer;
    @XmlElement(required = true)
    protected String model;
    protected boolean onlyCreated;
    protected String serial;

    /**
     * Gets the value of the ipRange property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpRange() {
        return ipRange;
    }

    /**
     * Sets the value of the ipRange property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpRange(String value) {
        this.ipRange = value;
    }

    /**
     * Gets the value of the manufacturer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the value of the manufacturer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManufacturer(String value) {
        this.manufacturer = value;
    }

    /**
     * Gets the value of the model property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the value of the model property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModel(String value) {
        this.model = value;
    }

    /**
     * Gets the value of the onlyCreated property.
     * 
     */
    public boolean isOnlyCreated() {
        return onlyCreated;
    }

    /**
     * Sets the value of the onlyCreated property.
     * 
     */
    public void setOnlyCreated(boolean value) {
        this.onlyCreated = value;
    }

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

}
