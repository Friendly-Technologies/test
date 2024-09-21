
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for paramMonitorListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paramMonitorListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="notification" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="paramMonitor" type="{http://ftacs.com/}paramMonitorWS" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paramMonitorListWS", propOrder = {
    "notification",
    "paramMonitor"
})
public class ParamMonitorListWS {

    protected Integer notification;
    @XmlElement(required = true)
    protected List<ParamMonitorWS> paramMonitor;

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
     * Gets the value of the paramMonitor property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paramMonitor property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParamMonitor().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ParamMonitorWS }
     * 
     * 
     */
    public List<ParamMonitorWS> getParamMonitor() {
        if (paramMonitor == null) {
            paramMonitor = new ArrayList<ParamMonitorWS>();
        }
        return this.paramMonitor;
    }

}
