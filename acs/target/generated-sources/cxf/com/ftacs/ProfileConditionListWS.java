
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for profileConditionListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profileConditionListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="profileCondition" type="{http://ftacs.com/}profileConditionWS" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileConditionListWS", propOrder = {
    "profileCondition"
})
public class ProfileConditionListWS {

    @XmlElement(required = true)
    protected List<ProfileConditionWS> profileCondition;

    /**
     * Gets the value of the profileCondition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the profileCondition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProfileCondition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProfileConditionWS }
     * 
     * 
     */
    public List<ProfileConditionWS> getProfileCondition() {
        if (profileCondition == null) {
            profileCondition = new ArrayList<ProfileConditionWS>();
        }
        return this.profileCondition;
    }

}
