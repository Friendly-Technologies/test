
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for addCPEToBlackList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="addCPEToBlackList"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cpeBlackList" type="{http://ftacs.com/}cpeBlackListWS" minOccurs="0"/&gt;
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
@XmlType(name = "addCPEToBlackList", propOrder = {
    "cpeBlackList",
    "user"
})
public class AddCPEToBlackList {

    protected CpeBlackListWS cpeBlackList;
    protected String user;

    /**
     * Gets the value of the cpeBlackList property.
     * 
     * @return
     *     possible object is
     *     {@link CpeBlackListWS }
     *     
     */
    public CpeBlackListWS getCpeBlackList() {
        return cpeBlackList;
    }

    /**
     * Sets the value of the cpeBlackList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpeBlackListWS }
     *     
     */
    public void setCpeBlackList(CpeBlackListWS value) {
        this.cpeBlackList = value;
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
