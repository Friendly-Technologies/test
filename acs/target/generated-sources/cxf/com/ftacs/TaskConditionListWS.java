
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for taskConditionListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="taskConditionListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="taskCondition" type="{http://ftacs.com/}taskConditionWS" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "taskConditionListWS", propOrder = {
    "taskCondition"
})
public class TaskConditionListWS {

    @XmlElement(required = true)
    protected List<TaskConditionWS> taskCondition;

    /**
     * Gets the value of the taskCondition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the taskCondition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTaskCondition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TaskConditionWS }
     * 
     * 
     */
    public List<TaskConditionWS> getTaskCondition() {
        if (taskCondition == null) {
            taskCondition = new ArrayList<TaskConditionWS>();
        }
        return this.taskCondition;
    }

}
