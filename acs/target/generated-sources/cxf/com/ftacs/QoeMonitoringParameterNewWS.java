
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for qoeMonitoringParameterNewWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="qoeMonitoringParameterNewWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="string" type="{http://ftacs.com/}qoeMonitoringParameterWS"/&gt;
 *         &lt;element name="attributes" type="{http://ftacs.com/}qoeMonitoringAttributesWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "qoeMonitoringParameterNewWS", propOrder = {
    "string",
    "attributes"
})
public class QoeMonitoringParameterNewWS {

    @XmlElement(required = true)
    protected QoeMonitoringParameterWS string;
    protected QoeMonitoringAttributesWS attributes;

    /**
     * Gets the value of the string property.
     * 
     * @return
     *     possible object is
     *     {@link QoeMonitoringParameterWS }
     *     
     */
    public QoeMonitoringParameterWS getString() {
        return string;
    }

    /**
     * Sets the value of the string property.
     * 
     * @param value
     *     allowed object is
     *     {@link QoeMonitoringParameterWS }
     *     
     */
    public void setString(QoeMonitoringParameterWS value) {
        this.string = value;
    }

    /**
     * Gets the value of the attributes property.
     * 
     * @return
     *     possible object is
     *     {@link QoeMonitoringAttributesWS }
     *     
     */
    public QoeMonitoringAttributesWS getAttributes() {
        return attributes;
    }

    /**
     * Sets the value of the attributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link QoeMonitoringAttributesWS }
     *     
     */
    public void setAttributes(QoeMonitoringAttributesWS value) {
        this.attributes = value;
    }

}
