
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for removeCPEProvisionData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="removeCPEProvisionData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cpeList" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *         &lt;element name="flags" type="{http://ftacs.com/}removeCPEProvisionDataFlags" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "removeCPEProvisionData", propOrder = {
    "cpeList",
    "flags"
})
public class RemoveCPEProvisionData {

    protected IntegerArrayWS cpeList;
    protected RemoveCPEProvisionDataFlags flags;

    /**
     * Gets the value of the cpeList property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getCpeList() {
        return cpeList;
    }

    /**
     * Sets the value of the cpeList property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setCpeList(IntegerArrayWS value) {
        this.cpeList = value;
    }

    /**
     * Gets the value of the flags property.
     * 
     * @return
     *     possible object is
     *     {@link RemoveCPEProvisionDataFlags }
     *     
     */
    public RemoveCPEProvisionDataFlags getFlags() {
        return flags;
    }

    /**
     * Sets the value of the flags property.
     * 
     * @param value
     *     allowed object is
     *     {@link RemoveCPEProvisionDataFlags }
     *     
     */
    public void setFlags(RemoveCPEProvisionDataFlags value) {
        this.flags = value;
    }

}
