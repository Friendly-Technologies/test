
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updateMonitoring complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateMonitoring"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="parentMonitoring" type="{http://ftacs.com/}parentMonitoringWS" minOccurs="0"/&gt;
 *         &lt;element name="parentMonitoringId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
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
@XmlType(name = "updateMonitoring", propOrder = {
    "parentMonitoring",
    "parentMonitoringId",
    "user"
})
public class UpdateMonitoring {

    protected ParentMonitoringWS parentMonitoring;
    protected Integer parentMonitoringId;
    protected String user;

    /**
     * Gets the value of the parentMonitoring property.
     * 
     * @return
     *     possible object is
     *     {@link ParentMonitoringWS }
     *     
     */
    public ParentMonitoringWS getParentMonitoring() {
        return parentMonitoring;
    }

    /**
     * Sets the value of the parentMonitoring property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParentMonitoringWS }
     *     
     */
    public void setParentMonitoring(ParentMonitoringWS value) {
        this.parentMonitoring = value;
    }

    /**
     * Gets the value of the parentMonitoringId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getParentMonitoringId() {
        return parentMonitoringId;
    }

    /**
     * Sets the value of the parentMonitoringId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setParentMonitoringId(Integer value) {
        this.parentMonitoringId = value;
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
