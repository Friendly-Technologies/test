
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for stopMonitoring complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="stopMonitoring"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="parentMonitoringId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="needReset" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "stopMonitoring", propOrder = {
    "parentMonitoringId",
    "needReset"
})
public class StopMonitoring {

    protected Integer parentMonitoringId;
    protected boolean needReset;

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
     * Gets the value of the needReset property.
     * 
     */
    public boolean isNeedReset() {
        return needReset;
    }

    /**
     * Sets the value of the needReset property.
     * 
     */
    public void setNeedReset(boolean value) {
        this.needReset = value;
    }

}
