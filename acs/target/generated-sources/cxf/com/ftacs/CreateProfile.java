
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for createProfile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createProfile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="profileList" type="{http://ftacs.com/}profileListWS" minOccurs="0"/&gt;
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
@XmlType(name = "createProfile", propOrder = {
    "profileList",
    "user"
})
public class CreateProfile {

    protected ProfileListWS profileList;
    protected String user;

    /**
     * Gets the value of the profileList property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileListWS }
     *     
     */
    public ProfileListWS getProfileList() {
        return profileList;
    }

    /**
     * Sets the value of the profileList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileListWS }
     *     
     */
    public void setProfileList(ProfileListWS value) {
        this.profileList = value;
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
