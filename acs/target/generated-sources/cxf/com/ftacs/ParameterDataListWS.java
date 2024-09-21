
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for parameterDataListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="parameterDataListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dataList" type="{http://ftacs.com/}stringArrayWS"/&gt;
 *         &lt;element name="parameterNameList" type="{http://ftacs.com/}stringArrayWS"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "parameterDataListWS", propOrder = {
    "dataList",
    "parameterNameList"
})
public class ParameterDataListWS {

    @XmlElement(required = true)
    protected StringArrayWS dataList;
    @XmlElement(required = true)
    protected StringArrayWS parameterNameList;

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
     * Gets the value of the parameterNameList property.
     * 
     * @return
     *     possible object is
     *     {@link StringArrayWS }
     *     
     */
    public StringArrayWS getParameterNameList() {
        return parameterNameList;
    }

    /**
     * Sets the value of the parameterNameList property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringArrayWS }
     *     
     */
    public void setParameterNameList(StringArrayWS value) {
        this.parameterNameList = value;
    }

}
