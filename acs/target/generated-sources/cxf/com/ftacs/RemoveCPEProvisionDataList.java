
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for removeCPEProvisionDataList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="removeCPEProvisionDataList"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cpeCustomRPCIds" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *         &lt;element name="cpeFileIds" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *         &lt;element name="cpeProvisionAttributeIds" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *         &lt;element name="cpeProvisionIds" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *         &lt;element name="cpeProvisionObjectIds" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "removeCPEProvisionDataList", propOrder = {
    "cpeCustomRPCIds",
    "cpeFileIds",
    "cpeProvisionAttributeIds",
    "cpeProvisionIds",
    "cpeProvisionObjectIds"
})
public class RemoveCPEProvisionDataList {

    protected IntegerArrayWS cpeCustomRPCIds;
    protected IntegerArrayWS cpeFileIds;
    protected IntegerArrayWS cpeProvisionAttributeIds;
    protected IntegerArrayWS cpeProvisionIds;
    protected IntegerArrayWS cpeProvisionObjectIds;

    /**
     * Gets the value of the cpeCustomRPCIds property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getCpeCustomRPCIds() {
        return cpeCustomRPCIds;
    }

    /**
     * Sets the value of the cpeCustomRPCIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setCpeCustomRPCIds(IntegerArrayWS value) {
        this.cpeCustomRPCIds = value;
    }

    /**
     * Gets the value of the cpeFileIds property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getCpeFileIds() {
        return cpeFileIds;
    }

    /**
     * Sets the value of the cpeFileIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setCpeFileIds(IntegerArrayWS value) {
        this.cpeFileIds = value;
    }

    /**
     * Gets the value of the cpeProvisionAttributeIds property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getCpeProvisionAttributeIds() {
        return cpeProvisionAttributeIds;
    }

    /**
     * Sets the value of the cpeProvisionAttributeIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setCpeProvisionAttributeIds(IntegerArrayWS value) {
        this.cpeProvisionAttributeIds = value;
    }

    /**
     * Gets the value of the cpeProvisionIds property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getCpeProvisionIds() {
        return cpeProvisionIds;
    }

    /**
     * Sets the value of the cpeProvisionIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setCpeProvisionIds(IntegerArrayWS value) {
        this.cpeProvisionIds = value;
    }

    /**
     * Gets the value of the cpeProvisionObjectIds property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getCpeProvisionObjectIds() {
        return cpeProvisionObjectIds;
    }

    /**
     * Sets the value of the cpeProvisionObjectIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setCpeProvisionObjectIds(IntegerArrayWS value) {
        this.cpeProvisionObjectIds = value;
    }

}
