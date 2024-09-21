
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for removeReceiverUrls complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="removeReceiverUrls"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="eventReceiverUrlIdsList" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "removeReceiverUrls", propOrder = {
    "eventReceiverUrlIdsList"
})
public class RemoveReceiverUrls {

    protected IntegerArrayWS eventReceiverUrlIdsList;

    /**
     * Gets the value of the eventReceiverUrlIdsList property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getEventReceiverUrlIdsList() {
        return eventReceiverUrlIdsList;
    }

    /**
     * Sets the value of the eventReceiverUrlIdsList property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setEventReceiverUrlIdsList(IntegerArrayWS value) {
        this.eventReceiverUrlIdsList = value;
    }

}
