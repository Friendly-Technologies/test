
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updateAccountInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateAccountInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="accountInfo" type="{http://ftacs.com/}custDeviceWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateAccountInfo", propOrder = {
    "accountInfo"
})
public class UpdateAccountInfo {

    protected CustDeviceWS accountInfo;

    /**
     * Gets the value of the accountInfo property.
     * 
     * @return
     *     possible object is
     *     {@link CustDeviceWS }
     *     
     */
    public CustDeviceWS getAccountInfo() {
        return accountInfo;
    }

    /**
     * Sets the value of the accountInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustDeviceWS }
     *     
     */
    public void setAccountInfo(CustDeviceWS value) {
        this.accountInfo = value;
    }

}
