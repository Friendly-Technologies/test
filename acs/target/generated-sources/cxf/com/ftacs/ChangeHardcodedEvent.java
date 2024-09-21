
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for changeHardcodedEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="changeHardcodedEvent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="eventHardcodedList" type="{http://ftacs.com/}eventHardcodedListWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "changeHardcodedEvent", propOrder = {
    "eventHardcodedList"
})
public class ChangeHardcodedEvent {

    protected EventHardcodedListWS eventHardcodedList;

    /**
     * Gets the value of the eventHardcodedList property.
     * 
     * @return
     *     possible object is
     *     {@link EventHardcodedListWS }
     *     
     */
    public EventHardcodedListWS getEventHardcodedList() {
        return eventHardcodedList;
    }

    /**
     * Sets the value of the eventHardcodedList property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventHardcodedListWS }
     *     
     */
    public void setEventHardcodedList(EventHardcodedListWS value) {
        this.eventHardcodedList = value;
    }

}
