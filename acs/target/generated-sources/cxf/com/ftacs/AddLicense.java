
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for addLicense complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="addLicense"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="newLicense" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addLicense", propOrder = {
    "newLicense"
})
public class AddLicense {

    protected String newLicense;

    /**
     * Gets the value of the newLicense property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewLicense() {
        return newLicense;
    }

    /**
     * Sets the value of the newLicense property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewLicense(String value) {
        this.newLicense = value;
    }

}
