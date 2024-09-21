
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cpeParamAttribWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cpeParamAttribWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="accessList" type="{http://ftacs.com/}accessListWS"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="notification" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="reprovision" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cpeParamAttribWS", propOrder = {
    "accessList",
    "name",
    "notification",
    "reprovision"
})
public class CpeParamAttribWS {

    @XmlElement(required = true)
    protected AccessListWS accessList;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer notification;
    protected boolean reprovision;

    /**
     * Gets the value of the accessList property.
     * 
     * @return
     *     possible object is
     *     {@link AccessListWS }
     *     
     */
    public AccessListWS getAccessList() {
        return accessList;
    }

    /**
     * Sets the value of the accessList property.
     * 
     * @param value
     *     allowed object is
     *     {@link AccessListWS }
     *     
     */
    public void setAccessList(AccessListWS value) {
        this.accessList = value;
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

    /**
     * Gets the value of the notification property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNotification() {
        return notification;
    }

    /**
     * Sets the value of the notification property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNotification(Integer value) {
        this.notification = value;
    }

    /**
     * Gets the value of the reprovision property.
     * 
     */
    public boolean isReprovision() {
        return reprovision;
    }

    /**
     * Sets the value of the reprovision property.
     * 
     */
    public void setReprovision(boolean value) {
        this.reprovision = value;
    }

}
