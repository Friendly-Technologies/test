
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for modifyProductClassGroupParameter complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="modifyProductClassGroupParameter"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="productClassGroupList" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *         &lt;element name="parameterList" type="{http://ftacs.com/}stringArrayWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modifyProductClassGroupParameter", propOrder = {
    "productClassGroupList",
    "parameterList"
})
public class ModifyProductClassGroupParameter {

    protected IntegerArrayWS productClassGroupList;
    protected StringArrayWS parameterList;

    /**
     * Gets the value of the productClassGroupList property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getProductClassGroupList() {
        return productClassGroupList;
    }

    /**
     * Sets the value of the productClassGroupList property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setProductClassGroupList(IntegerArrayWS value) {
        this.productClassGroupList = value;
    }

    /**
     * Gets the value of the parameterList property.
     * 
     * @return
     *     possible object is
     *     {@link StringArrayWS }
     *     
     */
    public StringArrayWS getParameterList() {
        return parameterList;
    }

    /**
     * Sets the value of the parameterList property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringArrayWS }
     *     
     */
    public void setParameterList(StringArrayWS value) {
        this.parameterList = value;
    }

}
