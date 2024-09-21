
package com.ftacs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for monitoringWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="monitoringWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="groupId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="cpeIds" type="{http://ftacs.com/}integerArrayWS"/&gt;
 *         &lt;element name="names" type="{http://ftacs.com/}qoeMonitoringParameterListWS"/&gt;
 *         &lt;element name="parameters" type="{http://ftacs.com/}qoeMonitoringParameterListNewWS"/&gt;
 *         &lt;element name="applyForNew" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="customViewId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="interval" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="monitoringAttributesStr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "monitoringWS", propOrder = {
    "id",
    "name",
    "groupId",
    "cpeIds",
    "names",
    "parameters",
    "applyForNew",
    "customViewId",
    "interval",
    "monitoringAttributesStr"
})
public class MonitoringWS {

    @XmlElementRef(name = "id", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> id;
    protected String name;
    protected int groupId;
    @XmlElement(required = true)
    protected IntegerArrayWS cpeIds;
    @XmlElement(required = true)
    protected QoeMonitoringParameterListWS names;
    @XmlElement(required = true)
    protected QoeMonitoringParameterListNewWS parameters;
    @XmlElement(required = true, type = Boolean.class, nillable = true)
    protected Boolean applyForNew;
    protected Integer customViewId;
    protected int interval;
    protected String monitoringAttributesStr;

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
     * Gets the value of the names property.
     * 
     * @return
     *     possible object is
     *     {@link QoeMonitoringParameterListWS }
     *     
     */
    public QoeMonitoringParameterListWS getNames() {
        return names;
    }

    /**
     * Sets the value of the names property.
     * 
     * @param value
     *     allowed object is
     *     {@link QoeMonitoringParameterListWS }
     *     
     */
    public void setNames(QoeMonitoringParameterListWS value) {
        this.names = value;
    }

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link QoeMonitoringParameterListNewWS }
     *     
     */
    public QoeMonitoringParameterListNewWS getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link QoeMonitoringParameterListNewWS }
     *     
     */
    public void setParameters(QoeMonitoringParameterListNewWS value) {
        this.parameters = value;
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
     * Gets the value of the interval property.
     * 
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Sets the value of the interval property.
     * 
     */
    public void setInterval(int value) {
        this.interval = value;
    }

    /**
     * Gets the value of the monitoringAttributesStr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonitoringAttributesStr() {
        return monitoringAttributesStr;
    }

    /**
     * Sets the value of the monitoringAttributesStr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonitoringAttributesStr(String value) {
        this.monitoringAttributesStr = value;
    }

}
