
package com.dm.friendly;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.ftacs.ProfileFileWS;


/**
 * <p>Java class for profileBackup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profileBackup"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ftacs.com/}profileFileWS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sendBackupForExisting" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileBackup", propOrder = {
    "sendBackupForExisting"
})
public class ProfileBackup
    extends ProfileFileWS
{

    @XmlElement(defaultValue = "true")
    protected Boolean sendBackupForExisting;

    /**
     * Gets the value of the sendBackupForExisting property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSendBackupForExisting() {
        return sendBackupForExisting;
    }

    /**
     * Sets the value of the sendBackupForExisting property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSendBackupForExisting(Boolean value) {
        this.sendBackupForExisting = value;
    }

}
