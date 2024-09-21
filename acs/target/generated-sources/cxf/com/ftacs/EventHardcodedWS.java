
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for eventHardcodedWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eventHardcodedWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="eventId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="notification" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="urlIds" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *         &lt;element name="ispId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventHardcodedWS", propOrder = {
    "eventId",
    "notification",
    "urlIds",
    "ispId"
})
public class EventHardcodedWS {

    protected int eventId;
    protected Integer notification;
    protected IntegerArrayWS urlIds;
    protected Integer ispId;

    /**
     * Gets the value of the eventId property.
     * 
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * Sets the value of the eventId property.
     * 
     */
    public void setEventId(int value) {
        this.eventId = value;
    }

    /**
     * Gets the value of the notification property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNotification() {
        return notification;
    }

    /**
     * Sets the value of the notification property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNotification(Integer value) {
        this.notification = value;
    }

    /**
     * Gets the value of the urlIds property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getUrlIds() {
        return urlIds;
    }

    /**
     * Sets the value of the urlIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setUrlIds(IntegerArrayWS value) {
        this.urlIds = value;
    }

    /**
     * Gets the value of the ispId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIspId() {
        return ispId;
    }

    /**
     * Sets the value of the ispId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIspId(Integer value) {
        this.ispId = value;
    }

}
