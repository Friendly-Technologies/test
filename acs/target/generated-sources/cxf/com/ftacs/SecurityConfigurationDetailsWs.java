
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for securityConfigurationDetailsWs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="securityConfigurationDetailsWs"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="clientRpk" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="login" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="privateKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="pskIdentity" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="pskSecretKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="securityModeType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="serverCertChain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="serverRpk" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="trustedCertChain" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="trustedCertChainAlias" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="underlyingProtocolType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "securityConfigurationDetailsWs", propOrder = {
    "clientRpk",
    "id",
    "login",
    "password",
    "privateKey",
    "pskIdentity",
    "pskSecretKey",
    "securityModeType",
    "serverCertChain",
    "serverRpk",
    "trustedCertChain",
    "trustedCertChainAlias",
    "underlyingProtocolType"
})
public class SecurityConfigurationDetailsWs {

    protected String clientRpk;
    protected Integer id;
    protected String login;
    protected String password;
    protected String privateKey;
    protected String pskIdentity;
    protected String pskSecretKey;
    protected String securityModeType;
    protected String serverCertChain;
    protected String serverRpk;
    protected String trustedCertChain;
    protected String trustedCertChainAlias;
    protected String underlyingProtocolType;

    /**
     * Gets the value of the clientRpk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientRpk() {
        return clientRpk;
    }

    /**
     * Sets the value of the clientRpk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientRpk(String value) {
        this.clientRpk = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setId(Integer value) {
        this.id = value;
    }

    /**
     * Gets the value of the login property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLogin() {
        return login;
    }

    /**
     * Sets the value of the login property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLogin(String value) {
        this.login = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the privateKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * Sets the value of the privateKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrivateKey(String value) {
        this.privateKey = value;
    }

    /**
     * Gets the value of the pskIdentity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPskIdentity() {
        return pskIdentity;
    }

    /**
     * Sets the value of the pskIdentity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPskIdentity(String value) {
        this.pskIdentity = value;
    }

    /**
     * Gets the value of the pskSecretKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPskSecretKey() {
        return pskSecretKey;
    }

    /**
     * Sets the value of the pskSecretKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPskSecretKey(String value) {
        this.pskSecretKey = value;
    }

    /**
     * Gets the value of the securityModeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecurityModeType() {
        return securityModeType;
    }

    /**
     * Sets the value of the securityModeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecurityModeType(String value) {
        this.securityModeType = value;
    }

    /**
     * Gets the value of the serverCertChain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServerCertChain() {
        return serverCertChain;
    }

    /**
     * Sets the value of the serverCertChain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServerCertChain(String value) {
        this.serverCertChain = value;
    }

    /**
     * Gets the value of the serverRpk property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServerRpk() {
        return serverRpk;
    }

    /**
     * Sets the value of the serverRpk property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServerRpk(String value) {
        this.serverRpk = value;
    }

    /**
     * Gets the value of the trustedCertChain property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrustedCertChain() {
        return trustedCertChain;
    }

    /**
     * Sets the value of the trustedCertChain property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrustedCertChain(String value) {
        this.trustedCertChain = value;
    }

    /**
     * Gets the value of the trustedCertChainAlias property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrustedCertChainAlias() {
        return trustedCertChainAlias;
    }

    /**
     * Sets the value of the trustedCertChainAlias property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrustedCertChainAlias(String value) {
        this.trustedCertChainAlias = value;
    }

    /**
     * Gets the value of the underlyingProtocolType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnderlyingProtocolType() {
        return underlyingProtocolType;
    }

    /**
     * Sets the value of the underlyingProtocolType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnderlyingProtocolType(String value) {
        this.underlyingProtocolType = value;
    }

}
