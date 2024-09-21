
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for profileParameterNotificationListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profileParameterNotificationListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="profileParameterNotification" type="{http://ftacs.com/}profileParameterNotificationWS" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileParameterNotificationListWS", propOrder = {
    "profileParameterNotification"
})
public class ProfileParameterNotificationListWS {

    @XmlElement(required = true)
    protected List<ProfileParameterNotificationWS> profileParameterNotification;

    /**
     * Gets the value of the profileParameterNotification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the profileParameterNotification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProfileParameterNotification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProfileParameterNotificationWS }
     * 
     * 
     */
    public List<ProfileParameterNotificationWS> getProfileParameterNotification() {
        if (profileParameterNotification == null) {
            profileParameterNotification = new ArrayList<ProfileParameterNotificationWS>();
        }
        return this.profileParameterNotification;
    }

}
