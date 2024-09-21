
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for profileEventMonitorListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profileEventMonitorListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="profileEventMonitor" type="{http://ftacs.com/}profileEventMonitorWS" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileEventMonitorListWS", propOrder = {
    "profileEventMonitor"
})
public class ProfileEventMonitorListWS {

    @XmlElement(required = true)
    protected List<ProfileEventMonitorWS> profileEventMonitor;

    /**
     * Gets the value of the profileEventMonitor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the profileEventMonitor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProfileEventMonitor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProfileEventMonitorWS }
     * 
     * 
     */
    public List<ProfileEventMonitorWS> getProfileEventMonitor() {
        if (profileEventMonitor == null) {
            profileEventMonitor = new ArrayList<ProfileEventMonitorWS>();
        }
        return this.profileEventMonitor;
    }

}
