
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fccTaskWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fccTaskWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ftacs.com/}updateTaskWS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="type" type="{http://ftacs.com/}fccTypeEnum" minOccurs="0"/&gt;
 *         &lt;element name="downloadParametersForGet" type="{http://ftacs.com/}stringArrayWS"/&gt;
 *         &lt;element name="downloadParametersForSet" type="{http://ftacs.com/}cpeParamListWS"/&gt;
 *         &lt;element name="uploadParametersForGet" type="{http://ftacs.com/}stringArrayWS"/&gt;
 *         &lt;element name="uploadParametersForSet" type="{http://ftacs.com/}cpeParamListWS"/&gt;
 *         &lt;element name="thresholdDownload" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="thresholdUpload" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="hubbId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tierDownload" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="tierUpload" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fccTaskWS", propOrder = {
    "type",
    "downloadParametersForGet",
    "downloadParametersForSet",
    "uploadParametersForGet",
    "uploadParametersForSet",
    "thresholdDownload",
    "thresholdUpload",
    "hubbId",
    "tierDownload",
    "tierUpload"
})
public class FccTaskWS
    extends UpdateTaskWS
{

    @XmlSchemaType(name = "string")
    protected FccTypeEnum type;
    @XmlElement(required = true)
    protected StringArrayWS downloadParametersForGet;
    @XmlElement(required = true)
    protected CpeParamListWS downloadParametersForSet;
    @XmlElement(required = true)
    protected StringArrayWS uploadParametersForGet;
    @XmlElement(required = true)
    protected CpeParamListWS uploadParametersForSet;
    protected int thresholdDownload;
    protected Integer thresholdUpload;
    protected String hubbId;
    protected int tierDownload;
    protected int tierUpload;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link FccTypeEnum }
     *     
     */
    public FccTypeEnum getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link FccTypeEnum }
     *     
     */
    public void setType(FccTypeEnum value) {
        this.type = value;
    }

    /**
     * Gets the value of the downloadParametersForGet property.
     * 
     * @return
     *     possible object is
     *     {@link StringArrayWS }
     *     
     */
    public StringArrayWS getDownloadParametersForGet() {
        return downloadParametersForGet;
    }

    /**
     * Sets the value of the downloadParametersForGet property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringArrayWS }
     *     
     */
    public void setDownloadParametersForGet(StringArrayWS value) {
        this.downloadParametersForGet = value;
    }

    /**
     * Gets the value of the downloadParametersForSet property.
     * 
     * @return
     *     possible object is
     *     {@link CpeParamListWS }
     *     
     */
    public CpeParamListWS getDownloadParametersForSet() {
        return downloadParametersForSet;
    }

    /**
     * Sets the value of the downloadParametersForSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpeParamListWS }
     *     
     */
    public void setDownloadParametersForSet(CpeParamListWS value) {
        this.downloadParametersForSet = value;
    }

    /**
     * Gets the value of the uploadParametersForGet property.
     * 
     * @return
     *     possible object is
     *     {@link StringArrayWS }
     *     
     */
    public StringArrayWS getUploadParametersForGet() {
        return uploadParametersForGet;
    }

    /**
     * Sets the value of the uploadParametersForGet property.
     * 
     * @param value
     *     allowed object is
     *     {@link StringArrayWS }
     *     
     */
    public void setUploadParametersForGet(StringArrayWS value) {
        this.uploadParametersForGet = value;
    }

    /**
     * Gets the value of the uploadParametersForSet property.
     * 
     * @return
     *     possible object is
     *     {@link CpeParamListWS }
     *     
     */
    public CpeParamListWS getUploadParametersForSet() {
        return uploadParametersForSet;
    }

    /**
     * Sets the value of the uploadParametersForSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpeParamListWS }
     *     
     */
    public void setUploadParametersForSet(CpeParamListWS value) {
        this.uploadParametersForSet = value;
    }

    /**
     * Gets the value of the thresholdDownload property.
     * 
     */
    public int getThresholdDownload() {
        return thresholdDownload;
    }

    /**
     * Sets the value of the thresholdDownload property.
     * 
     */
    public void setThresholdDownload(int value) {
        this.thresholdDownload = value;
    }

    /**
     * Gets the value of the thresholdUpload property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getThresholdUpload() {
        return thresholdUpload;
    }

    /**
     * Sets the value of the thresholdUpload property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setThresholdUpload(Integer value) {
        this.thresholdUpload = value;
    }

    /**
     * Gets the value of the hubbId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHubbId() {
        return hubbId;
    }

    /**
     * Sets the value of the hubbId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHubbId(String value) {
        this.hubbId = value;
    }

    /**
     * Gets the value of the tierDownload property.
     * 
     */
    public int getTierDownload() {
        return tierDownload;
    }

    /**
     * Sets the value of the tierDownload property.
     * 
     */
    public void setTierDownload(int value) {
        this.tierDownload = value;
    }

    /**
     * Gets the value of the tierUpload property.
     * 
     */
    public int getTierUpload() {
        return tierUpload;
    }

    /**
     * Sets the value of the tierUpload property.
     * 
     */
    public void setTierUpload(int value) {
        this.tierUpload = value;
    }

}
