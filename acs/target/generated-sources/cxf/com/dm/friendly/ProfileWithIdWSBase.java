
package com.dm.friendly;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import com.ftacs.ProfileWithIdWS;


/**
 * <p>Java class for profileWithIdWSBase complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profileWithIdWSBase"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://friendly.dm.com/}profileWS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="profileId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileWithIdWSBase", propOrder = {
    "profileId"
})
@XmlSeeAlso({
    ProfileWithIdWS.class
})
public class ProfileWithIdWSBase
    extends ProfileWS
{

    protected Integer profileId;

    /**
     * Gets the value of the profileId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getProfileId() {
        return profileId;
    }

    /**
     * Sets the value of the profileId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setProfileId(Integer value) {
        this.profileId = value;
    }

}
