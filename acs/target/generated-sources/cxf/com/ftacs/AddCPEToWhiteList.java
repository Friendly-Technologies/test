
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for addCPEToWhiteList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="addCPEToWhiteList"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cpeWhiteList" type="{http://ftacs.com/}cpeWhiteListWS" minOccurs="0"/&gt;
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
@XmlType(name = "addCPEToWhiteList", propOrder = {
    "cpeWhiteList",
    "user"
})
public class AddCPEToWhiteList {

    protected CpeWhiteListWS cpeWhiteList;
    protected String user;

    /**
     * Gets the value of the cpeWhiteList property.
     * 
     * @return
     *     possible object is
     *     {@link CpeWhiteListWS }
     *     
     */
    public CpeWhiteListWS getCpeWhiteList() {
        return cpeWhiteList;
    }

    /**
     * Sets the value of the cpeWhiteList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpeWhiteListWS }
     *     
     */
    public void setCpeWhiteList(CpeWhiteListWS value) {
        this.cpeWhiteList = value;
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
