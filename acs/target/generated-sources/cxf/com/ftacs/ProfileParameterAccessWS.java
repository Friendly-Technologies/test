
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for profileParameterAccessWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profileParameterAccessWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="accessList" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="accessListChange" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileParameterAccessWS", propOrder = {
    "accessList",
    "accessListChange",
    "name"
})
public class ProfileParameterAccessWS {

    @XmlElement(required = true)
    protected String accessList;
    protected int accessListChange;
    @XmlElement(required = true)
    protected String name;

    /**
     * Gets the value of the accessList property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccessList() {
        return accessList;
    }

    /**
     * Sets the value of the accessList property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccessList(String value) {
        this.accessList = value;
    }

    /**
     * Gets the value of the accessListChange property.
     * 
     */
    public int getAccessListChange() {
        return accessListChange;
    }

    /**
     * Sets the value of the accessListChange property.
     * 
     */
    public void setAccessListChange(int value) {
        this.accessListChange = value;
    }

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

}
