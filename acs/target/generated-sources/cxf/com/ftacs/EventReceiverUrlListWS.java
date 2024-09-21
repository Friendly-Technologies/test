
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for eventReceiverUrlListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eventReceiverUrlListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="eventReceiverUrl" type="{http://ftacs.com/}eventReceiverUrlWS" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventReceiverUrlListWS", propOrder = {
    "eventReceiverUrl"
})
public class EventReceiverUrlListWS {

    @XmlElement(required = true)
    protected List<EventReceiverUrlWS> eventReceiverUrl;

    /**
     * Gets the value of the eventReceiverUrl property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eventReceiverUrl property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEventReceiverUrl().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EventReceiverUrlWS }
     * 
     * 
     */
    public List<EventReceiverUrlWS> getEventReceiverUrl() {
        if (eventReceiverUrl == null) {
            eventReceiverUrl = new ArrayList<EventReceiverUrlWS>();
        }
        return this.eventReceiverUrl;
    }

}
