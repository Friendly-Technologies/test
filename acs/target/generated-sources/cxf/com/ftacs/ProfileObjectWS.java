
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for profileObjectWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profileObjectWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="copies" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="parentId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="parentInstance" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="profileObjectParamList" type="{http://ftacs.com/}profileObjectParamListWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileObjectWS", propOrder = {
    "copies",
    "description",
    "name",
    "parentId",
    "parentInstance",
    "profileObjectParamList"
})
public class ProfileObjectWS {

    protected Integer copies;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true)
    protected String name;
    protected Integer parentId;
    protected Integer parentInstance;
    protected ProfileObjectParamListWS profileObjectParamList;

    /**
     * Gets the value of the copies property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCopies() {
        return copies;
    }

    /**
     * Sets the value of the copies property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCopies(Integer value) {
        this.copies = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the parentId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * Sets the value of the parentId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setParentId(Integer value) {
        this.parentId = value;
    }

    /**
     * Gets the value of the parentInstance property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getParentInstance() {
        return parentInstance;
    }

    /**
     * Sets the value of the parentInstance property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setParentInstance(Integer value) {
        this.parentInstance = value;
    }

    /**
     * Gets the value of the profileObjectParamList property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileObjectParamListWS }
     *     
     */
    public ProfileObjectParamListWS getProfileObjectParamList() {
        return profileObjectParamList;
    }

    /**
     * Sets the value of the profileObjectParamList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileObjectParamListWS }
     *     
     */
    public void setProfileObjectParamList(ProfileObjectParamListWS value) {
        this.profileObjectParamList = value;
    }

}
