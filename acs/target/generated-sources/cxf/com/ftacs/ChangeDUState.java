
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for changeDUState complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="changeDUState"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cpeList" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *         &lt;element name="installOperations" type="{http://ftacs.com/}installOpListWS" minOccurs="0"/&gt;
 *         &lt;element name="updateOperations" type="{http://ftacs.com/}updateOpListWS" minOccurs="0"/&gt;
 *         &lt;element name="unInstallOperations" type="{http://ftacs.com/}unInstallOpListWS" minOccurs="0"/&gt;
 *         &lt;element name="priority" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
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
@XmlType(name = "changeDUState", propOrder = {
    "cpeList",
    "installOperations",
    "updateOperations",
    "unInstallOperations",
    "priority",
    "push",
    "reset",
    "transactionId",
    "user"
})
public class ChangeDUState {

    protected IntegerArrayWS cpeList;
    protected InstallOpListWS installOperations;
    protected UpdateOpListWS updateOperations;
    protected UnInstallOpListWS unInstallOperations;
    protected Integer priority;
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
     * Gets the value of the installOperations property.
     * 
     * @return
     *     possible object is
     *     {@link InstallOpListWS }
     *     
     */
    public InstallOpListWS getInstallOperations() {
        return installOperations;
    }

    /**
     * Sets the value of the installOperations property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstallOpListWS }
     *     
     */
    public void setInstallOperations(InstallOpListWS value) {
        this.installOperations = value;
    }

    /**
     * Gets the value of the updateOperations property.
     * 
     * @return
     *     possible object is
     *     {@link UpdateOpListWS }
     *     
     */
    public UpdateOpListWS getUpdateOperations() {
        return updateOperations;
    }

    /**
     * Sets the value of the updateOperations property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateOpListWS }
     *     
     */
    public void setUpdateOperations(UpdateOpListWS value) {
        this.updateOperations = value;
    }

    /**
     * Gets the value of the unInstallOperations property.
     * 
     * @return
     *     possible object is
     *     {@link UnInstallOpListWS }
     *     
     */
    public UnInstallOpListWS getUnInstallOperations() {
        return unInstallOperations;
    }

    /**
     * Sets the value of the unInstallOperations property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnInstallOpListWS }
     *     
     */
    public void setUnInstallOperations(UnInstallOpListWS value) {
        this.unInstallOperations = value;
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
