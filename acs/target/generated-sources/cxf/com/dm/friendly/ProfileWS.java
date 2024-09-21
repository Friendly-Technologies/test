
package com.dm.friendly;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import com.ftacs.ProfileConditionListWS;
import com.ftacs.ProfileFileListWS;
import com.ftacs.ProfileObjectListWS;
import com.ftacs.ProfileOptionListWS;
import com.ftacs.ProfileParameterAccessListWS;
import com.ftacs.ProfileParameterListWS;
import com.ftacs.ProfileParameterNotificationListWS;


/**
 * <p>Java class for profileWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profileWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="fullTree" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="productClassGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="profileConditionList" type="{http://ftacs.com/}profileConditionListWS" minOccurs="0"/&gt;
 *         &lt;element name="profileFileList" type="{http://ftacs.com/}profileFileListWS" minOccurs="0"/&gt;
 *         &lt;element name="profileObjectList" type="{http://ftacs.com/}profileObjectListWS" minOccurs="0"/&gt;
 *         &lt;element name="profileOptionList" type="{http://ftacs.com/}profileOptionListWS" minOccurs="0"/&gt;
 *         &lt;element name="profileParameterAccessList" type="{http://ftacs.com/}profileParameterAccessListWS" minOccurs="0"/&gt;
 *         &lt;element name="profileParameterList" type="{http://ftacs.com/}profileParameterListWS" minOccurs="0"/&gt;
 *         &lt;element name="profileParameterNotificationList" type="{http://ftacs.com/}profileParameterNotificationListWS" minOccurs="0"/&gt;
 *         &lt;element name="profileBackup" type="{http://friendly.dm.com/}profileBackup" minOccurs="0"/&gt;
 *         &lt;element name="sendProvision" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="isActive" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="locationId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileWS", propOrder = {
    "fullTree",
    "name",
    "productClassGroupId",
    "profileConditionList",
    "profileFileList",
    "profileObjectList",
    "profileOptionList",
    "profileParameterAccessList",
    "profileParameterList",
    "profileParameterNotificationList",
    "profileBackup",
    "sendProvision",
    "isActive",
    "version",
    "locationId"
})
@XmlSeeAlso({
    com.ftacs.ProfileWS.class,
    ProfileWithIdWSBase.class
})
public class ProfileWS {

    protected int fullTree;
    @XmlElement(required = true)
    protected String name;
    protected int productClassGroupId;
    protected ProfileConditionListWS profileConditionList;
    protected ProfileFileListWS profileFileList;
    protected ProfileObjectListWS profileObjectList;
    protected ProfileOptionListWS profileOptionList;
    protected ProfileParameterAccessListWS profileParameterAccessList;
    protected ProfileParameterListWS profileParameterList;
    protected ProfileParameterNotificationListWS profileParameterNotificationList;
    protected ProfileBackup profileBackup;
    protected Boolean sendProvision;
    protected Boolean isActive;
    @XmlElement(required = true)
    protected String version;
    @XmlElement(defaultValue = "0")
    protected Integer locationId;

    /**
     * Gets the value of the fullTree property.
     * 
     */
    public int getFullTree() {
        return fullTree;
    }

    /**
     * Sets the value of the fullTree property.
     * 
     */
    public void setFullTree(int value) {
        this.fullTree = value;
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
     * Gets the value of the productClassGroupId property.
     * 
     */
    public int getProductClassGroupId() {
        return productClassGroupId;
    }

    /**
     * Sets the value of the productClassGroupId property.
     * 
     */
    public void setProductClassGroupId(int value) {
        this.productClassGroupId = value;
    }

    /**
     * Gets the value of the profileConditionList property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileConditionListWS }
     *     
     */
    public ProfileConditionListWS getProfileConditionList() {
        return profileConditionList;
    }

    /**
     * Sets the value of the profileConditionList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileConditionListWS }
     *     
     */
    public void setProfileConditionList(ProfileConditionListWS value) {
        this.profileConditionList = value;
    }

    /**
     * Gets the value of the profileFileList property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileFileListWS }
     *     
     */
    public ProfileFileListWS getProfileFileList() {
        return profileFileList;
    }

    /**
     * Sets the value of the profileFileList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileFileListWS }
     *     
     */
    public void setProfileFileList(ProfileFileListWS value) {
        this.profileFileList = value;
    }

    /**
     * Gets the value of the profileObjectList property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileObjectListWS }
     *     
     */
    public ProfileObjectListWS getProfileObjectList() {
        return profileObjectList;
    }

    /**
     * Sets the value of the profileObjectList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileObjectListWS }
     *     
     */
    public void setProfileObjectList(ProfileObjectListWS value) {
        this.profileObjectList = value;
    }

    /**
     * Gets the value of the profileOptionList property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileOptionListWS }
     *     
     */
    public ProfileOptionListWS getProfileOptionList() {
        return profileOptionList;
    }

    /**
     * Sets the value of the profileOptionList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileOptionListWS }
     *     
     */
    public void setProfileOptionList(ProfileOptionListWS value) {
        this.profileOptionList = value;
    }

    /**
     * Gets the value of the profileParameterAccessList property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileParameterAccessListWS }
     *     
     */
    public ProfileParameterAccessListWS getProfileParameterAccessList() {
        return profileParameterAccessList;
    }

    /**
     * Sets the value of the profileParameterAccessList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileParameterAccessListWS }
     *     
     */
    public void setProfileParameterAccessList(ProfileParameterAccessListWS value) {
        this.profileParameterAccessList = value;
    }

    /**
     * Gets the value of the profileParameterList property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileParameterListWS }
     *     
     */
    public ProfileParameterListWS getProfileParameterList() {
        return profileParameterList;
    }

    /**
     * Sets the value of the profileParameterList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileParameterListWS }
     *     
     */
    public void setProfileParameterList(ProfileParameterListWS value) {
        this.profileParameterList = value;
    }

    /**
     * Gets the value of the profileParameterNotificationList property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileParameterNotificationListWS }
     *     
     */
    public ProfileParameterNotificationListWS getProfileParameterNotificationList() {
        return profileParameterNotificationList;
    }

    /**
     * Sets the value of the profileParameterNotificationList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileParameterNotificationListWS }
     *     
     */
    public void setProfileParameterNotificationList(ProfileParameterNotificationListWS value) {
        this.profileParameterNotificationList = value;
    }

    /**
     * Gets the value of the profileBackup property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileBackup }
     *     
     */
    public ProfileBackup getProfileBackup() {
        return profileBackup;
    }

    /**
     * Sets the value of the profileBackup property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileBackup }
     *     
     */
    public void setProfileBackup(ProfileBackup value) {
        this.profileBackup = value;
    }

    /**
     * Gets the value of the sendProvision property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSendProvision() {
        return sendProvision;
    }

    /**
     * Sets the value of the sendProvision property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSendProvision(Boolean value) {
        this.sendProvision = value;
    }

    /**
     * Gets the value of the isActive property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsActive() {
        return isActive;
    }

    /**
     * Sets the value of the isActive property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsActive(Boolean value) {
        this.isActive = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the locationId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLocationId() {
        return locationId;
    }

    /**
     * Sets the value of the locationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLocationId(Integer value) {
        this.locationId = value;
    }

}
