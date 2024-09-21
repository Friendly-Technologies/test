
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getTask complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getTask"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ftacs.com/}updateTaskWS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="parameters" type="{http://ftacs.com/}getEntryListWS"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTask", propOrder = {
    "parameters"
})
public class GetTask
    extends UpdateTaskWS
{

    @XmlElement(required = true)
    protected GetEntryListWS parameters;

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link GetEntryListWS }
     *     
     */
    public GetEntryListWS getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetEntryListWS }
     *     
     */
    public void setParameters(GetEntryListWS value) {
        this.parameters = value;
    }

}
