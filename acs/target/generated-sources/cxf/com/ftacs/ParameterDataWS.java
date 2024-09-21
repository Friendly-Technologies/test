
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for parameterDataWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="parameterDataWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dataList" type="{http://ftacs.com/}stringArrayWS"/&gt;
 *         &lt;element name="parameterName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "parameterDataWS", propOrder = {
    "dataList",
    "parameterName"
})
public class ParameterDataWS {

    @XmlElement(required = true)
    protected StringArrayWS dataList;
    @XmlElement(required = true)
    protected String parameterName;

    /**
     * Gets the value of the dataList property.
     * 
     * @return
     *     possible object is
     *     {@link StringArrayWS }
     *     
     */
    public StringArrayWS getDataList() {
        return dataList;
    }

    /**
     * Sets the value of the dataList property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringArrayWS }
     *     
     */
    public void setDataList(StringArrayWS value) {
        this.dataList = value;
    }

    /**
     * Gets the value of the parameterName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * Sets the value of the parameterName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParameterName(String value) {
        this.parameterName = value;
    }

}
