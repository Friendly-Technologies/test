
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for profileConditionWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profileConditionWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="custDeviceFieldList" type="{http://ftacs.com/}custDeviceFieldListWS" minOccurs="0"/&gt;
 *         &lt;element name="parameterList" type="{http://ftacs.com/}parameterListWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileConditionWS", propOrder = {
    "custDeviceFieldList",
    "parameterList"
})
public class ProfileConditionWS {

    protected CustDeviceFieldListWS custDeviceFieldList;
    protected ParameterListWS parameterList;

    /**
     * Gets the value of the custDeviceFieldList property.
     * 
     * @return
     *     possible object is
     *     {@link CustDeviceFieldListWS }
     *     
     */
    public CustDeviceFieldListWS getCustDeviceFieldList() {
        return custDeviceFieldList;
    }

    /**
     * Sets the value of the custDeviceFieldList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustDeviceFieldListWS }
     *     
     */
    public void setCustDeviceFieldList(CustDeviceFieldListWS value) {
        this.custDeviceFieldList = value;
    }

    /**
     * Gets the value of the parameterList property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterListWS }
     *     
     */
    public ParameterListWS getParameterList() {
        return parameterList;
    }

    /**
     * Sets the value of the parameterList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterListWS }
     *     
     */
    public void setParameterList(ParameterListWS value) {
        this.parameterList = value;
    }

}
