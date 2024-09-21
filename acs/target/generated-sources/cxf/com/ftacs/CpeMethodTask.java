
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cpeMethodTask complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cpeMethodTask"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ftacs.com/}updateTaskWS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cpeMethod" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="parameters" type="{http://ftacs.com/}parameterListWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cpeMethodTask", propOrder = {
    "cpeMethod",
    "parameters"
})
public class CpeMethodTask
    extends UpdateTaskWS
{

    @XmlElement(required = true)
    protected String cpeMethod;
    protected ParameterListWS parameters;

    /**
     * Gets the value of the cpeMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCpeMethod() {
        return cpeMethod;
    }

    /**
     * Sets the value of the cpeMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCpeMethod(String value) {
        this.cpeMethod = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterListWS }
     *     
     */
    public ParameterListWS getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterListWS }
     *     
     */
    public void setParameters(ParameterListWS value) {
        this.parameters = value;
    }

}
