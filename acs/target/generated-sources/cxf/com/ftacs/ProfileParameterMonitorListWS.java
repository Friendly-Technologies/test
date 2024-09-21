
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for profileParameterMonitorListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profileParameterMonitorListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="notification" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="profileParameterMonitor" type="{http://ftacs.com/}profileParameterMonitorWS" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileParameterMonitorListWS", propOrder = {
    "notification",
    "profileParameterMonitor"
})
public class ProfileParameterMonitorListWS {

    protected Integer notification;
    @XmlElement(required = true)
    protected List<ProfileParameterMonitorWS> profileParameterMonitor;

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
     * Gets the value of the profileParameterMonitor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the profileParameterMonitor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProfileParameterMonitor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProfileParameterMonitorWS }
     * 
     * 
     */
    public List<ProfileParameterMonitorWS> getProfileParameterMonitor() {
        if (profileParameterMonitor == null) {
            profileParameterMonitor = new ArrayList<ProfileParameterMonitorWS>();
        }
        return this.profileParameterMonitor;
    }

}
