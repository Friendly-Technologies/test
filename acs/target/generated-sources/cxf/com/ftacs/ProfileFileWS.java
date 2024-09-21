
package com.ftacs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import com.dm.friendly.ProfileBackup;


/**
 * <p>Java class for profileFileWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profileFileWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="delaySeconds" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="failureURL" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="fileName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="fileSize" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="fileTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="successURL" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="targetFileName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="reset" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="newest" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="sendBytes" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="fileVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="deliveryProtocol" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="deliveryMethod" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileFileWS", propOrder = {
    "delaySeconds",
    "failureURL",
    "fileName",
    "fileSize",
    "fileTypeId",
    "password",
    "successURL",
    "targetFileName",
    "url",
    "username",
    "reset",
    "newest",
    "sendBytes",
    "fileVersion",
    "deliveryProtocol",
    "deliveryMethod"
})
@XmlSeeAlso({
    ProfileBackup.class
})
public class ProfileFileWS {

    protected int delaySeconds;
    @XmlElement(required = true)
    protected String failureURL;
    @XmlElement(required = true)
    protected String fileName;
    protected int fileSize;
    protected int fileTypeId;
    @XmlElement(required = true)
    protected String password;
    @XmlElement(required = true)
    protected String successURL;
    @XmlElement(required = true)
    protected String targetFileName;
    @XmlElement(required = true)
    protected String url;
    @XmlElement(required = true)
    protected String username;
    @XmlElement(required = true, type = Boolean.class, defaultValue = "false", nillable = true)
    protected Boolean reset;
    protected Boolean newest;
    @XmlElement(required = true, type = Boolean.class, defaultValue = "false", nillable = true)
    protected Boolean sendBytes;
    protected String fileVersion;
    @XmlElementRef(name = "deliveryProtocol", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> deliveryProtocol;
    @XmlElementRef(name = "deliveryMethod", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> deliveryMethod;

    /**
     * Gets the value of the delaySeconds property.
     * 
     */
    public int getDelaySeconds() {
        return delaySeconds;
    }

    /**
     * Sets the value of the delaySeconds property.
     * 
     */
    public void setDelaySeconds(int value) {
        this.delaySeconds = value;
    }

    /**
     * Gets the value of the failureURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFailureURL() {
        return failureURL;
    }

    /**
     * Sets the value of the failureURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFailureURL(String value) {
        this.failureURL = value;
    }

    /**
     * Gets the value of the fileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the value of the fileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

    /**
     * Gets the value of the fileSize property.
     * 
     */
    public int getFileSize() {
        return fileSize;
    }

    /**
     * Sets the value of the fileSize property.
     * 
     */
    public void setFileSize(int value) {
        this.fileSize = value;
    }

    /**
     * Gets the value of the fileTypeId property.
     * 
     */
    public int getFileTypeId() {
        return fileTypeId;
    }

    /**
     * Sets the value of the fileTypeId property.
     * 
     */
    public void setFileTypeId(int value) {
        this.fileTypeId = value;
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
     * Gets the value of the successURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuccessURL() {
        return successURL;
    }

    /**
     * Sets the value of the successURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuccessURL(String value) {
        this.successURL = value;
    }

    /**
     * Gets the value of the targetFileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetFileName() {
        return targetFileName;
    }

    /**
     * Sets the value of the targetFileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetFileName(String value) {
        this.targetFileName = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrl(String value) {
        this.url = value;
    }

    /**
     * Gets the value of the username property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the value of the username property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

    /**
     * Gets the value of the reset property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReset() {
        return reset;
    }

    /**
     * Sets the value of the reset property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReset(Boolean value) {
        this.reset = value;
    }

    /**
     * Gets the value of the newest property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNewest() {
        return newest;
    }

    /**
     * Sets the value of the newest property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNewest(Boolean value) {
        this.newest = value;
    }

    /**
     * Gets the value of the sendBytes property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSendBytes() {
        return sendBytes;
    }

    /**
     * Sets the value of the sendBytes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSendBytes(Boolean value) {
        this.sendBytes = value;
    }

    /**
     * Gets the value of the fileVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileVersion() {
        return fileVersion;
    }

    /**
     * Sets the value of the fileVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileVersion(String value) {
        this.fileVersion = value;
    }

    /**
     * Gets the value of the deliveryProtocol property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getDeliveryProtocol() {
        return deliveryProtocol;
    }

    /**
     * Sets the value of the deliveryProtocol property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setDeliveryProtocol(JAXBElement<Integer> value) {
        this.deliveryProtocol = value;
    }

    /**
     * Gets the value of the deliveryMethod property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getDeliveryMethod() {
        return deliveryMethod;
    }

    /**
     * Sets the value of the deliveryMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setDeliveryMethod(JAXBElement<Integer> value) {
        this.deliveryMethod = value;
    }

}
