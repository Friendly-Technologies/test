
package com.ftacs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cpeUploadFile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cpeUploadFile"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="delaySeconds" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="fileName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="fileTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="username" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="reprovision" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="instance" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cpeUploadFile", propOrder = {
    "delaySeconds",
    "fileName",
    "fileTypeId",
    "password",
    "url",
    "username",
    "reprovision",
    "instance"
})
public class CpeUploadFile {

    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer delaySeconds;
    @XmlElement(required = true, nillable = true)
    protected String fileName;
    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer fileTypeId;
    @XmlElement(required = true, nillable = true)
    protected String password;
    @XmlElement(required = true, nillable = true)
    protected String url;
    @XmlElement(required = true, nillable = true)
    protected String username;
    @XmlElement(required = true, type = Boolean.class, nillable = true)
    protected Boolean reprovision;
    @XmlElementRef(name = "instance", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> instance;

    /**
     * Gets the value of the delaySeconds property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDelaySeconds() {
        return delaySeconds;
    }

    /**
     * Sets the value of the delaySeconds property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDelaySeconds(Integer value) {
        this.delaySeconds = value;
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
     * Gets the value of the fileTypeId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFileTypeId() {
        return fileTypeId;
    }

    /**
     * Sets the value of the fileTypeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFileTypeId(Integer value) {
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
     * Gets the value of the reprovision property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReprovision() {
        return reprovision;
    }

    /**
     * Sets the value of the reprovision property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReprovision(Boolean value) {
        this.reprovision = value;
    }

    /**
     * Gets the value of the instance property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getInstance() {
        return instance;
    }

    /**
     * Sets the value of the instance property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setInstance(JAXBElement<Integer> value) {
        this.instance = value;
    }

}
