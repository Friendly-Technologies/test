
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for setAttributesTask complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="setAttributesTask"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ftacs.com/}updateTaskWS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="parameters" type="{http://ftacs.com/}cpeParamAttribListWS"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "setAttributesTask", propOrder = {
    "parameters"
})
public class SetAttributesTask
    extends UpdateTaskWS
{

    @XmlElement(required = true)
    protected CpeParamAttribListWS parameters;

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link CpeParamAttribListWS }
     *     
     */
    public CpeParamAttribListWS getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpeParamAttribListWS }
     *     
     */
    public void setParameters(CpeParamAttribListWS value) {
        this.parameters = value;
    }

}
