
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for discoverSNMPCpe complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="discoverSNMPCpe"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="filterStruct" type="{http://ftacs.com/}snmpFilterStruct" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "discoverSNMPCpe", propOrder = {
    "filterStruct"
})
public class DiscoverSNMPCpe {

    protected SnmpFilterStruct filterStruct;

    /**
     * Gets the value of the filterStruct property.
     * 
     * @return
     *     possible object is
     *     {@link SnmpFilterStruct }
     *     
     */
    public SnmpFilterStruct getFilterStruct() {
        return filterStruct;
    }

    /**
     * Sets the value of the filterStruct property.
     * 
     * @param value
     *     allowed object is
     *     {@link SnmpFilterStruct }
     *     
     */
    public void setFilterStruct(SnmpFilterStruct value) {
        this.filterStruct = value;
    }

}
