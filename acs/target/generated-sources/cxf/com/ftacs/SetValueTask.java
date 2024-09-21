
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for setValueTask complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="setValueTask"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ftacs.com/}updateTaskWS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="parameters" type="{http://ftacs.com/}cpeParamListWS"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "setValueTask", propOrder = {
    "parameters"
})
public class SetValueTask
    extends UpdateTaskWS
{

    @XmlElement(required = true)
    protected CpeParamListWS parameters;

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link CpeParamListWS }
     *     
     */
    public CpeParamListWS getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpeParamListWS }
     *     
     */
    public void setParameters(CpeParamListWS value) {
        this.parameters = value;
    }

}
