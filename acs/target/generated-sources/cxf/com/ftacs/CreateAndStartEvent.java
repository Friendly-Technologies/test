
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createAndStartEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createAndStartEvent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="parentEvent" type="{http://ftacs.com/}parentEventWS" minOccurs="0"/&gt;
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createAndStartEvent", propOrder = {
    "parentEvent",
    "user"
})
public class CreateAndStartEvent {

    protected ParentEventWS parentEvent;
    protected String user;

    /**
     * Gets the value of the parentEvent property.
     * 
     * @return
     *     possible object is
     *     {@link ParentEventWS }
     *     
     */
    public ParentEventWS getParentEvent() {
        return parentEvent;
    }

    /**
     * Sets the value of the parentEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParentEventWS }
     *     
     */
    public void setParentEvent(ParentEventWS value) {
        this.parentEvent = value;
    }

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

}