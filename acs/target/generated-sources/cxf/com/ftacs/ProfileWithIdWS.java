
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import com.dm.friendly.ProfileWithIdWSBase;


/**
 * <p>Java class for profileWithIdWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profileWithIdWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://friendly.dm.com/}profileWithIdWSBase"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="profileCollectorList" type="{http://ftacs.com/}profileCollectorListWS" minOccurs="0"/&gt;
 *         &lt;element name="profileEventMonitorList" type="{http://ftacs.com/}profileEventMonitorListWS" minOccurs="0"/&gt;
 *         &lt;element name="profileParameterMonitorList" type="{http://ftacs.com/}profileParameterMonitorListWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileWithIdWS", propOrder = {
    "profileCollectorList",
    "profileEventMonitorList",
    "profileParameterMonitorList"
})
public class ProfileWithIdWS
    extends ProfileWithIdWSBase
{

    protected ProfileCollectorListWS profileCollectorList;
    protected ProfileEventMonitorListWS profileEventMonitorList;
    protected ProfileParameterMonitorListWS profileParameterMonitorList;

    /**
     * Gets the value of the profileCollectorList property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileCollectorListWS }
     *     
     */
    public ProfileCollectorListWS getProfileCollectorList() {
        return profileCollectorList;
    }

    /**
     * Sets the value of the profileCollectorList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileCollectorListWS }
     *     
     */
    public void setProfileCollectorList(ProfileCollectorListWS value) {
        this.profileCollectorList = value;
    }

    /**
     * Gets the value of the profileEventMonitorList property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileEventMonitorListWS }
     *     
     */
    public ProfileEventMonitorListWS getProfileEventMonitorList() {
        return profileEventMonitorList;
    }

    /**
     * Sets the value of the profileEventMonitorList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileEventMonitorListWS }
     *     
     */
    public void setProfileEventMonitorList(ProfileEventMonitorListWS value) {
        this.profileEventMonitorList = value;
    }

    /**
     * Gets the value of the profileParameterMonitorList property.
     * 
     * @return
     *     possible object is
     *     {@link ProfileParameterMonitorListWS }
     *     
     */
    public ProfileParameterMonitorListWS getProfileParameterMonitorList() {
        return profileParameterMonitorList;
    }

    /**
     * Sets the value of the profileParameterMonitorList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProfileParameterMonitorListWS }
     *     
     */
    public void setProfileParameterMonitorList(ProfileParameterMonitorListWS value) {
        this.profileParameterMonitorList = value;
    }

}
