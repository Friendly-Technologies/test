
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updateGroupChildWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateGroupChildWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="groupId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="cpeIds" type="{http://ftacs.com/}integerArrayWS"/&gt;
 *         &lt;element name="tasks" type="{http://ftacs.com/}updateGroupTaskWSList" minOccurs="0"/&gt;
 *         &lt;element name="customViewId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateGroupChildWS", propOrder = {
    "groupId",
    "cpeIds",
    "tasks",
    "customViewId"
})
public class UpdateGroupChildWS {

    protected Integer groupId;
    @XmlElement(required = true)
    protected IntegerArrayWS cpeIds;
    protected UpdateGroupTaskWSList tasks;
    protected Integer customViewId;

    /**
     * Gets the value of the groupId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     * Sets the value of the groupId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setGroupId(Integer value) {
        this.groupId = value;
    }

    /**
     * Gets the value of the cpeIds property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getCpeIds() {
        return cpeIds;
    }

    /**
     * Sets the value of the cpeIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setCpeIds(IntegerArrayWS value) {
        this.cpeIds = value;
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

    /**
     * Gets the value of the customViewId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCustomViewId() {
        return customViewId;
    }

    /**
     * Sets the value of the customViewId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCustomViewId(Integer value) {
        this.customViewId = value;
    }

}
