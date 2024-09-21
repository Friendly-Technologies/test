
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteManufacturerAndModel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteManufacturerAndModel"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="manufacturerAndModelList" type="{http://ftacs.com/}manufacturerAndModelListWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteManufacturerAndModel", propOrder = {
    "manufacturerAndModelList"
})
public class DeleteManufacturerAndModel {

    protected ManufacturerAndModelListWS manufacturerAndModelList;

    /**
     * Gets the value of the manufacturerAndModelList property.
     * 
     * @return
     *     possible object is
     *     {@link ManufacturerAndModelListWS }
     *     
     */
    public ManufacturerAndModelListWS getManufacturerAndModelList() {
        return manufacturerAndModelList;
    }

    /**
     * Sets the value of the manufacturerAndModelList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ManufacturerAndModelListWS }
     *     
     */
    public void setManufacturerAndModelList(ManufacturerAndModelListWS value) {
        this.manufacturerAndModelList = value;
    }

}
