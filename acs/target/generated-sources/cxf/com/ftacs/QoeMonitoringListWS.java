
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for qoeMonitoringListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="qoeMonitoringListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="monitoringList" type="{http://ftacs.com/}monitoringWS" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "qoeMonitoringListWS", propOrder = {
    "monitoringList"
})
public class QoeMonitoringListWS {

    @XmlElement(nillable = true)
    protected List<MonitoringWS> monitoringList;

    /**
     * Gets the value of the monitoringList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the monitoringList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMonitoringList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MonitoringWS }
     * 
     * 
     */
    public List<MonitoringWS> getMonitoringList() {
        if (monitoringList == null) {
            monitoringList = new ArrayList<MonitoringWS>();
        }
        return this.monitoringList;
    }

}
