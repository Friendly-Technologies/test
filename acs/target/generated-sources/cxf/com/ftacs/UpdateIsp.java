
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updateIsp complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateIsp"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ispsList" type="{http://ftacs.com/}ispWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateIsp", propOrder = {
    "ispsList"
})
public class UpdateIsp {

    protected IspWS ispsList;

    /**
     * Gets the value of the ispsList property.
     * 
     * @return
     *     possible object is
     *     {@link IspWS }
     *     
     */
    public IspWS getIspsList() {
        return ispsList;
    }

    /**
     * Sets the value of the ispsList property.
     * 
     * @param value
     *     allowed object is
     *     {@link IspWS }
     *     
     */
    public void setIspsList(IspWS value) {
        this.ispsList = value;
    }

}
