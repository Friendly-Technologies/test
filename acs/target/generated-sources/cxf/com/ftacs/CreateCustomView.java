
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createCustomView complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createCustomView"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="customView" type="{http://ftacs.com/}customView" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createCustomView", propOrder = {
    "customView"
})
public class CreateCustomView {

    protected CustomView customView;

    /**
     * Gets the value of the customView property.
     * 
     * @return
     *     possible object is
     *     {@link CustomView }
     *     
     */
    public CustomView getCustomView() {
        return customView;
    }

    /**
     * Sets the value of the customView property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomView }
     *     
     */
    public void setCustomView(CustomView value) {
        this.customView = value;
    }

}
