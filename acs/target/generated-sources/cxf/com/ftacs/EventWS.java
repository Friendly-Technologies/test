
package com.ftacs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for eventWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eventWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="groupId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="cpeIds" type="{http://ftacs.com/}integerArrayWS"/&gt;
 *         &lt;element name="applyForNew" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="customViewId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="paramMonitorListWS" type="{http://ftacs.com/}paramMonitorListWS" minOccurs="0"/&gt;
 *         &lt;element name="eventMonitorListWS" type="{http://ftacs.com/}eventMonitorListWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventWS", propOrder = {
    "id",
    "name",
    "groupId",
    "cpeIds",
    "applyForNew",
    "customViewId",
    "paramMonitorListWS",
    "eventMonitorListWS"
})
public class EventWS {

    @XmlElementRef(name = "id", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> id;
    protected String name;
    protected int groupId;
    @XmlElement(required = true)
    protected IntegerArrayWS cpeIds;
    @XmlElement(required = true, type = Boolean.class, nillable = true)
    protected Boolean applyForNew;
    protected Integer customViewId;
    protected ParamMonitorListWS paramMonitorListWS;
    protected EventMonitorListWS eventMonitorListWS;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setId(JAXBElement<Integer> value) {
        this.id = value;
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
     * Gets the value of the groupId property.
     * 
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * Sets the value of the groupId property.
     * 
     */
    public void setGroupId(int value) {
        this.groupId = value;
    }

    /**
     * Gets the value of the cpeIds property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getCpeIds() {
        return cpeIds;
    }

    /**
     * Sets the value of the cpeIds property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setCpeIds(IntegerArrayWS value) {
        this.cpeIds = value;
    }

    /**
     * Gets the value of the applyForNew property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isApplyForNew() {
        return applyForNew;
    }

    /**
     * Sets the value of the applyForNew property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setApplyForNew(Boolean value) {
        this.applyForNew = value;
    }

    /**
     * Gets the value of the customViewId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCustomViewId() {
        return customViewId;
    }

    /**
     * Sets the value of the customViewId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCustomViewId(Integer value) {
        this.customViewId = value;
    }

    /**
     * Gets the value of the paramMonitorListWS property.
     * 
     * @return
     *     possible object is
     *     {@link ParamMonitorListWS }
     *     
     */
    public ParamMonitorListWS getParamMonitorListWS() {
        return paramMonitorListWS;
    }

    /**
     * Sets the value of the paramMonitorListWS property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParamMonitorListWS }
     *     
     */
    public void setParamMonitorListWS(ParamMonitorListWS value) {
        this.paramMonitorListWS = value;
    }

    /**
     * Gets the value of the eventMonitorListWS property.
     * 
     * @return
     *     possible object is
     *     {@link EventMonitorListWS }
     *     
     */
    public EventMonitorListWS getEventMonitorListWS() {
        return eventMonitorListWS;
    }

    /**
     * Sets the value of the eventMonitorListWS property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventMonitorListWS }
     *     
     */
    public void setEventMonitorListWS(EventMonitorListWS value) {
        this.eventMonitorListWS = value;
    }

}
