
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for addManufacturer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="addManufacturer"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="manufacturerList" type="{http://ftacs.com/}manufacturerListWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addManufacturer", propOrder = {
    "manufacturerList"
})
public class AddManufacturer {

    protected ManufacturerListWS manufacturerList;

    /**
     * Gets the value of the manufacturerList property.
     * 
     * @return
     *     possible object is
     *     {@link ManufacturerListWS }
     *     
     */
    public ManufacturerListWS getManufacturerList() {
        return manufacturerList;
    }

    /**
     * Sets the value of the manufacturerList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ManufacturerListWS }
     *     
     */
    public void setManufacturerList(ManufacturerListWS value) {
        this.manufacturerList = value;
    }

}
