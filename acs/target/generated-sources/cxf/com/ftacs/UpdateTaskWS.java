
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updateTaskWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateTaskWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="order" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="taskConditions" type="{http://ftacs.com/}taskConditionListWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateTaskWS", propOrder = {
    "order",
    "taskConditions"
})
@XmlSeeAlso({
    GetTask.class,
    SetValueTask.class,
    SetAttributesTask.class,
    RebootTask.class,
    FactoryResetTask.class,
    RpcMethodTask.class,
    ReprovisionTask.class,
    DownloadTask.class,
    ChangeDUSateTask.class,
    DiagnosticTaskWS.class,
    CallApiTask.class,
    AddToProvisionTask.class,
    UdpPingTask.class,
    FccTaskWS.class,
    UploadTask.class,
    CpeMethodTask.class
})
public abstract class UpdateTaskWS {

    protected Integer order;
    protected TaskConditionListWS taskConditions;

    /**
     * Gets the value of the order property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOrder() {
        return order;
    }

    /**
     * Sets the value of the order property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOrder(Integer value) {
        this.order = value;
    }

    /**
     * Gets the value of the taskConditions property.
     * 
     * @return
     *     possible object is
     *     {@link TaskConditionListWS }
     *     
     */
    public TaskConditionListWS getTaskConditions() {
        return taskConditions;
    }

    /**
     * Sets the value of the taskConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link TaskConditionListWS }
     *     
     */
    public void setTaskConditions(TaskConditionListWS value) {
        this.taskConditions = value;
    }

}
