
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for stopSOAPTracingForUnknownCPEs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="stopSOAPTracingForUnknownCPEs"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cpeList" type="{http://ftacs.com/}cpesoapTracingListWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "stopSOAPTracingForUnknownCPEs", propOrder = {
    "cpeList"
})
public class StopSOAPTracingForUnknownCPEs {

    protected CpesoapTracingListWS cpeList;

    /**
     * Gets the value of the cpeList property.
     * 
     * @return
     *     possible object is
     *     {@link CpesoapTracingListWS }
     *     
     */
    public CpesoapTracingListWS getCpeList() {
        return cpeList;
    }

    /**
     * Sets the value of the cpeList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpesoapTracingListWS }
     *     
     */
    public void setCpeList(CpesoapTracingListWS value) {
        this.cpeList = value;
    }

}
