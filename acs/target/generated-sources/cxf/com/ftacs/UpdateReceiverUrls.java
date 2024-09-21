
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updateReceiverUrls complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateReceiverUrls"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="eventReceiverUrlList" type="{http://ftacs.com/}eventReceiverUrlListWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateReceiverUrls", propOrder = {
    "eventReceiverUrlList"
})
public class UpdateReceiverUrls {

    protected EventReceiverUrlListWS eventReceiverUrlList;

    /**
     * Gets the value of the eventReceiverUrlList property.
     * 
     * @return
     *     possible object is
     *     {@link EventReceiverUrlListWS }
     *     
     */
    public EventReceiverUrlListWS getEventReceiverUrlList() {
        return eventReceiverUrlList;
    }

    /**
     * Sets the value of the eventReceiverUrlList property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventReceiverUrlListWS }
     *     
     */
    public void setEventReceiverUrlList(EventReceiverUrlListWS value) {
        this.eventReceiverUrlList = value;
    }

}
