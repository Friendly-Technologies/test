
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for removeCPEProvisionDataFlags complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="removeCPEProvisionDataFlags"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="removeCPECustomRPCs" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="removeCPEFiles" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="removeCPEProvision" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="removeCPEProvisionAttributes" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="removeCPEProvisionObjects" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "removeCPEProvisionDataFlags", propOrder = {
    "removeCPECustomRPCs",
    "removeCPEFiles",
    "removeCPEProvision",
    "removeCPEProvisionAttributes",
    "removeCPEProvisionObjects"
})
public class RemoveCPEProvisionDataFlags {

    protected Boolean removeCPECustomRPCs;
    protected Boolean removeCPEFiles;
    protected Boolean removeCPEProvision;
    protected Boolean removeCPEProvisionAttributes;
    protected Boolean removeCPEProvisionObjects;

    /**
     * Gets the value of the removeCPECustomRPCs property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRemoveCPECustomRPCs() {
        return removeCPECustomRPCs;
    }

    /**
     * Sets the value of the removeCPECustomRPCs property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRemoveCPECustomRPCs(Boolean value) {
        this.removeCPECustomRPCs = value;
    }

    /**
     * Gets the value of the removeCPEFiles property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRemoveCPEFiles() {
        return removeCPEFiles;
    }

    /**
     * Sets the value of the removeCPEFiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRemoveCPEFiles(Boolean value) {
        this.removeCPEFiles = value;
    }

    /**
     * Gets the value of the removeCPEProvision property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRemoveCPEProvision() {
        return removeCPEProvision;
    }

    /**
     * Sets the value of the removeCPEProvision property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRemoveCPEProvision(Boolean value) {
        this.removeCPEProvision = value;
    }

    /**
     * Gets the value of the removeCPEProvisionAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRemoveCPEProvisionAttributes() {
        return removeCPEProvisionAttributes;
    }

    /**
     * Sets the value of the removeCPEProvisionAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRemoveCPEProvisionAttributes(Boolean value) {
        this.removeCPEProvisionAttributes = value;
    }

    /**
     * Gets the value of the removeCPEProvisionObjects property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRemoveCPEProvisionObjects() {
        return removeCPEProvisionObjects;
    }

    /**
     * Sets the value of the removeCPEProvisionObjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRemoveCPEProvisionObjects(Boolean value) {
        this.removeCPEProvisionObjects = value;
    }

}
