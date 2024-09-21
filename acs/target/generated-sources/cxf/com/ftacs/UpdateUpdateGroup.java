
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updateUpdateGroup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateUpdateGroup"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="updateGroup" type="{http://ftacs.com/}updateGroupWS" minOccurs="0"/&gt;
 *         &lt;element name="updateGroupId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateUpdateGroup", propOrder = {
    "updateGroup",
    "updateGroupId",
    "user"
})
public class UpdateUpdateGroup {

    protected UpdateGroupWS updateGroup;
    protected Integer updateGroupId;
    protected String user;

    /**
     * Gets the value of the updateGroup property.
     * 
     * @return
     *     possible object is
     *     {@link UpdateGroupWS }
     *     
     */
    public UpdateGroupWS getUpdateGroup() {
        return updateGroup;
    }

    /**
     * Sets the value of the updateGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateGroupWS }
     *     
     */
    public void setUpdateGroup(UpdateGroupWS value) {
        this.updateGroup = value;
    }

    /**
     * Gets the value of the updateGroupId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUpdateGroupId() {
        return updateGroupId;
    }

    /**
     * Sets the value of the updateGroupId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUpdateGroupId(Integer value) {
        this.updateGroupId = value;
    }

    /**
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

}
