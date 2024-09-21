
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for diagnosticTaskWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="diagnosticTaskWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ftacs.com/}updateTaskWS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="parametersForGet" type="{http://ftacs.com/}stringArrayWS"/&gt;
 *         &lt;element name="parametersForSet" type="{http://ftacs.com/}cpeParamListWS"/&gt;
 *         &lt;element name="qoeTask" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "diagnosticTaskWS", propOrder = {
    "name",
    "parametersForGet",
    "parametersForSet",
    "qoeTask"
})
public class DiagnosticTaskWS
    extends UpdateTaskWS
{

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected StringArrayWS parametersForGet;
    @XmlElement(required = true)
    protected CpeParamListWS parametersForSet;
    protected boolean qoeTask;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the parametersForGet property.
     * 
     * @return
     *     possible object is
     *     {@link StringArrayWS }
     *     
     */
    public StringArrayWS getParametersForGet() {
        return parametersForGet;
    }

    /**
     * Sets the value of the parametersForGet property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringArrayWS }
     *     
     */
    public void setParametersForGet(StringArrayWS value) {
        this.parametersForGet = value;
    }

    /**
     * Gets the value of the parametersForSet property.
     * 
     * @return
     *     possible object is
     *     {@link CpeParamListWS }
     *     
     */
    public CpeParamListWS getParametersForSet() {
        return parametersForSet;
    }

    /**
     * Sets the value of the parametersForSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpeParamListWS }
     *     
     */
    public void setParametersForSet(CpeParamListWS value) {
        this.parametersForSet = value;
    }

    /**
     * Gets the value of the qoeTask property.
     * 
     */
    public boolean isQoeTask() {
        return qoeTask;
    }

    /**
     * Sets the value of the qoeTask property.
     * 
     */
    public void setQoeTask(boolean value) {
        this.qoeTask = value;
    }

}
