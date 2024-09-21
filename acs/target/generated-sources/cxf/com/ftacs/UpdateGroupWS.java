
package com.ftacs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for updateGroupWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateGroupWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="randomCnt" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="scheduled" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="threshold" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="onlineOnly" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="push" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="stopOnFail" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="reactivateOptions" type="{http://ftacs.com/}reactivateWS" minOccurs="0"/&gt;
 *         &lt;element name="periods" type="{http://ftacs.com/}periodListWS" minOccurs="0"/&gt;
 *         &lt;element name="updateGroupChildList" type="{http://ftacs.com/}updateGroupChildListWS"/&gt;
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
@XmlType(name = "updateGroupWS", propOrder = {
    "name",
    "randomCnt",
    "scheduled",
    "threshold",
    "onlineOnly",
    "push",
    "stopOnFail",
    "reactivateOptions",
    "periods",
    "updateGroupChildList",
    "locationId"
})
public class UpdateGroupWS {

    protected String name;
    protected Integer randomCnt;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar scheduled;
    protected Integer threshold;
    protected boolean onlineOnly;
    protected boolean push;
    protected boolean stopOnFail;
    @XmlElementRef(name = "reactivateOptions", type = JAXBElement.class, required = false)
    protected JAXBElement<ReactivateWS> reactivateOptions;
    protected PeriodListWS periods;
    @XmlElement(required = true)
    protected UpdateGroupChildListWS updateGroupChildList;
    @XmlElement(defaultValue = "0")
    protected Integer locationId;

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
     * Gets the value of the randomCnt property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRandomCnt() {
        return randomCnt;
    }

    /**
     * Sets the value of the randomCnt property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRandomCnt(Integer value) {
        this.randomCnt = value;
    }

    /**
     * Gets the value of the scheduled property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getScheduled() {
        return scheduled;
    }

    /**
     * Sets the value of the scheduled property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setScheduled(XMLGregorianCalendar value) {
        this.scheduled = value;
    }

    /**
     * Gets the value of the threshold property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getThreshold() {
        return threshold;
    }

    /**
     * Sets the value of the threshold property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setThreshold(Integer value) {
        this.threshold = value;
    }

    /**
     * Gets the value of the onlineOnly property.
     * 
     */
    public boolean isOnlineOnly() {
        return onlineOnly;
    }

    /**
     * Sets the value of the onlineOnly property.
     * 
     */
    public void setOnlineOnly(boolean value) {
        this.onlineOnly = value;
    }

    /**
     * Gets the value of the push property.
     * 
     */
    public boolean isPush() {
        return push;
    }

    /**
     * Sets the value of the push property.
     * 
     */
    public void setPush(boolean value) {
        this.push = value;
    }

    /**
     * Gets the value of the stopOnFail property.
     * 
     */
    public boolean isStopOnFail() {
        return stopOnFail;
    }

    /**
     * Sets the value of the stopOnFail property.
     * 
     */
    public void setStopOnFail(boolean value) {
        this.stopOnFail = value;
    }

    /**
     * Gets the value of the reactivateOptions property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ReactivateWS }{@code >}
     *     
     */
    public JAXBElement<ReactivateWS> getReactivateOptions() {
        return reactivateOptions;
    }

    /**
     * Sets the value of the reactivateOptions property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ReactivateWS }{@code >}
     *     
     */
    public void setReactivateOptions(JAXBElement<ReactivateWS> value) {
        this.reactivateOptions = value;
    }

    /**
     * Gets the value of the periods property.
     * 
     * @return
     *     possible object is
     *     {@link PeriodListWS }
     *     
     */
    public PeriodListWS getPeriods() {
        return periods;
    }

    /**
     * Sets the value of the periods property.
     * 
     * @param value
     *     allowed object is
     *     {@link PeriodListWS }
     *     
     */
    public void setPeriods(PeriodListWS value) {
        this.periods = value;
    }

    /**
     * Gets the value of the updateGroupChildList property.
     * 
     * @return
     *     possible object is
     *     {@link UpdateGroupChildListWS }
     *     
     */
    public UpdateGroupChildListWS getUpdateGroupChildList() {
        return updateGroupChildList;
    }

    /**
     * Sets the value of the updateGroupChildList property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateGroupChildListWS }
     *     
     */
    public void setUpdateGroupChildList(UpdateGroupChildListWS value) {
        this.updateGroupChildList = value;
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
