
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for paramMonitorWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="paramMonitorWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="condition" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="paramName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="paramValue" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="tasks" type="{http://ftacs.com/}updateGroupTaskWSList" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "paramMonitorWS", propOrder = {
    "condition",
    "paramName",
    "paramValue",
    "tasks"
})
public class ParamMonitorWS {

    @XmlElement(required = true)
    protected String condition;
    @XmlElement(required = true)
    protected String paramName;
    @XmlElement(required = true)
    protected String paramValue;
    protected UpdateGroupTaskWSList tasks;

    /**
     * Gets the value of the condition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Sets the value of the condition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCondition(String value) {
        this.condition = value;
    }

    /**
     * Gets the value of the paramName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParamName() {
        return paramName;
    }

    /**
     * Sets the value of the paramName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParamName(String value) {
        this.paramName = value;
    }

    /**
     * Gets the value of the paramValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParamValue() {
        return paramValue;
    }

    /**
     * Sets the value of the paramValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParamValue(String value) {
        this.paramValue = value;
    }

    /**
     * Gets the value of the tasks property.
     * 
     * @return
     *     possible object is
     *     {@link UpdateGroupTaskWSList }
     *     
     */
    public UpdateGroupTaskWSList getTasks() {
        return tasks;
    }

    /**
     * Sets the value of the tasks property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateGroupTaskWSList }
     *     
     */
    public void setTasks(UpdateGroupTaskWSList value) {
        this.tasks = value;
    }

}
