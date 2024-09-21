
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reprovisionCPE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reprovisionCPE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cpeList" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *         &lt;element name="sendProfile" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="sendCPEProvision" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="sendCPEProvisionAttribute" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="customRPC" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="cpeProvisionObject" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="cpeFile" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
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
@XmlType(name = "reprovisionCPE", propOrder = {
    "cpeList",
    "sendProfile",
    "sendCPEProvision",
    "sendCPEProvisionAttribute",
    "customRPC",
    "cpeProvisionObject",
    "cpeFile",
    "transactionId",
    "user"
})
public class ReprovisionCPE {

    protected IntegerArrayWS cpeList;
    protected boolean sendProfile;
    protected boolean sendCPEProvision;
    protected boolean sendCPEProvisionAttribute;
    protected boolean customRPC;
    protected boolean cpeProvisionObject;
    protected boolean cpeFile;
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
     * Gets the value of the sendProfile property.
     * 
     */
    public boolean isSendProfile() {
        return sendProfile;
    }

    /**
     * Sets the value of the sendProfile property.
     * 
     */
    public void setSendProfile(boolean value) {
        this.sendProfile = value;
    }

    /**
     * Gets the value of the sendCPEProvision property.
     * 
     */
    public boolean isSendCPEProvision() {
        return sendCPEProvision;
    }

    /**
     * Sets the value of the sendCPEProvision property.
     * 
     */
    public void setSendCPEProvision(boolean value) {
        this.sendCPEProvision = value;
    }

    /**
     * Gets the value of the sendCPEProvisionAttribute property.
     * 
     */
    public boolean isSendCPEProvisionAttribute() {
        return sendCPEProvisionAttribute;
    }

    /**
     * Sets the value of the sendCPEProvisionAttribute property.
     * 
     */
    public void setSendCPEProvisionAttribute(boolean value) {
        this.sendCPEProvisionAttribute = value;
    }

    /**
     * Gets the value of the customRPC property.
     * 
     */
    public boolean isCustomRPC() {
        return customRPC;
    }

    /**
     * Sets the value of the customRPC property.
     * 
     */
    public void setCustomRPC(boolean value) {
        this.customRPC = value;
    }

    /**
     * Gets the value of the cpeProvisionObject property.
     * 
     */
    public boolean isCpeProvisionObject() {
        return cpeProvisionObject;
    }

    /**
     * Sets the value of the cpeProvisionObject property.
     * 
     */
    public void setCpeProvisionObject(boolean value) {
        this.cpeProvisionObject = value;
    }

    /**
     * Gets the value of the cpeFile property.
     * 
     */
    public boolean isCpeFile() {
        return cpeFile;
    }

    /**
     * Sets the value of the cpeFile property.
     * 
     */
    public void setCpeFile(boolean value) {
        this.cpeFile = value;
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
