
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for eventReceiverUrlAuthWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eventReceiverUrlAuthWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="authUrl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="clientId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="clientSecret" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="authType" type="{http://ftacs.com/}authType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventReceiverUrlAuthWS", propOrder = {
    "authUrl",
    "clientId",
    "clientSecret",
    "authType"
})
public class EventReceiverUrlAuthWS {

    protected String authUrl;
    protected String clientId;
    protected String clientSecret;
    @XmlSchemaType(name = "string")
    protected AuthType authType;

    /**
     * Gets the value of the authUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthUrl() {
        return authUrl;
    }

    /**
     * Sets the value of the authUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthUrl(String value) {
        this.authUrl = value;
    }

    /**
     * Gets the value of the clientId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the value of the clientId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientId(String value) {
        this.clientId = value;
    }

    /**
     * Gets the value of the clientSecret property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Sets the value of the clientSecret property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientSecret(String value) {
        this.clientSecret = value;
    }

    /**
     * Gets the value of the authType property.
     * 
     * @return
     *     possible object is
     *     {@link AuthType }
     *     
     */
    public AuthType getAuthType() {
        return authType;
    }

    /**
     * Sets the value of the authType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthType }
     *     
     */
    public void setAuthType(AuthType value) {
        this.authType = value;
    }

}
