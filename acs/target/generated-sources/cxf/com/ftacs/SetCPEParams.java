
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for setCPEParams complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="setCPEParams"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cpeList" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *         &lt;element name="cpeParamList" type="{http://ftacs.com/}cpeParamListWS" minOccurs="0"/&gt;
 *         &lt;element name="priority" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="group" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="push" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="reset" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="transactionId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
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
@XmlType(name = "setCPEParams", propOrder = {
    "cpeList",
    "cpeParamList",
    "priority",
    "group",
    "push",
    "reset",
    "transactionId",
    "user"
})
public class SetCPEParams {

    protected IntegerArrayWS cpeList;
    protected CpeParamListWS cpeParamList;
    protected Integer priority;
    protected boolean group;
    protected boolean push;
    protected boolean reset;
    protected Long transactionId;
    protected String user;

    /**
     * Gets the value of the cpeList property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getCpeList() {
        return cpeList;
    }

    /**
     * Sets the value of the cpeList property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setCpeList(IntegerArrayWS value) {
        this.cpeList = value;
    }

    /**
     * Gets the value of the cpeParamList property.
     * 
     * @return
     *     possible object is
     *     {@link CpeParamListWS }
     *     
     */
    public CpeParamListWS getCpeParamList() {
        return cpeParamList;
    }

    /**
     * Sets the value of the cpeParamList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpeParamListWS }
     *     
     */
    public void setCpeParamList(CpeParamListWS value) {
        this.cpeParamList = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPriority(Integer value) {
        this.priority = value;
    }

    /**
     * Gets the value of the group property.
     * 
     */
    public boolean isGroup() {
        return group;
    }

    /**
     * Sets the value of the group property.
     * 
     */
    public void setGroup(boolean value) {
        this.group = value;
    }

    /**
     * Gets the value of the push property.
     * 
     */
    public boolean isPush() {
        return push;
    }

    /**
     * Sets the value of the push property.
     * 
     */
    public void setPush(boolean value) {
        this.push = value;
    }

    /**
     * Gets the value of the reset property.
     * 
     */
    public boolean isReset() {
        return reset;
    }

    /**
     * Sets the value of the reset property.
     * 
     */
    public void setReset(boolean value) {
        this.reset = value;
    }

    /**
     * Gets the value of the transactionId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTransactionId(Long value) {
        this.transactionId = value;
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
