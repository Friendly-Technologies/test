
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteLWM2MResourceDefinitions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteLWM2MResourceDefinitions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="objectDbIds" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *         &lt;element name="resourceDbIds" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
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
@XmlType(name = "deleteLWM2MResourceDefinitions", propOrder = {
    "user",
    "objectDbIds",
    "resourceDbIds",
    "locationId"
})
public class DeleteLWM2MResourceDefinitions {

    protected String user;
    protected IntegerArrayWS objectDbIds;
    protected IntegerArrayWS resourceDbIds;
    protected Integer locationId;

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Gets the value of the objectDbIds property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getObjectDbIds() {
        return objectDbIds;
    }

    /**
     * Sets the value of the objectDbIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setObjectDbIds(IntegerArrayWS value) {
        this.objectDbIds = value;
    }

    /**
     * Gets the value of the resourceDbIds property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getResourceDbIds() {
        return resourceDbIds;
    }

    /**
     * Sets the value of the resourceDbIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setResourceDbIds(IntegerArrayWS value) {
        this.resourceDbIds = value;
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
