
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for reprovisionTask complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reprovisionTask"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ftacs.com/}updateTaskWS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sendProfile" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="sendCPEProvision" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="sendCPEProvisionAttribute" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="customRPC" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="cpeProvisionObject" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="cpeFile" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reprovisionTask", propOrder = {
    "sendProfile",
    "sendCPEProvision",
    "sendCPEProvisionAttribute",
    "customRPC",
    "cpeProvisionObject",
    "cpeFile"
})
public class ReprovisionTask
    extends UpdateTaskWS
{

    protected boolean sendProfile;
    protected boolean sendCPEProvision;
    protected boolean sendCPEProvisionAttribute;
    protected boolean customRPC;
    protected boolean cpeProvisionObject;
    protected boolean cpeFile;

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

}
