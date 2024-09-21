
package com.ftacs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for udpPingTask complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="udpPingTask"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ftacs.com/}updateTaskWS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="startTime" type="{http://ftacs.com/}timeInfo" minOccurs="0"/&gt;
 *         &lt;element name="endTime" type="{http://ftacs.com/}timeInfo" minOccurs="0"/&gt;
 *         &lt;element name="interval" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="probes" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ipAddressRefresh" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ipAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="port" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "udpPingTask", propOrder = {
    "duration",
    "startTime",
    "endTime",
    "interval",
    "probes",
    "ipAddressRefresh",
    "ipAddress",
    "port",
    "name"
})
public class UdpPingTask
    extends UpdateTaskWS
{

    protected Integer duration;
    @XmlElementRef(name = "startTime", type = JAXBElement.class, required = false)
    protected JAXBElement<TimeInfo> startTime;
    @XmlElementRef(name = "endTime", type = JAXBElement.class, required = false)
    protected JAXBElement<TimeInfo> endTime;
    protected Integer interval;
    protected Integer probes;
    protected Integer ipAddressRefresh;
    protected String ipAddress;
    protected Integer port;
    protected String name;

    /**
     * Gets the value of the duration property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * Sets the value of the duration property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDuration(Integer value) {
        this.duration = value;
    }

    /**
     * Gets the value of the startTime property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TimeInfo }{@code >}
     *     
     */
    public JAXBElement<TimeInfo> getStartTime() {
        return startTime;
    }

    /**
     * Sets the value of the startTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TimeInfo }{@code >}
     *     
     */
    public void setStartTime(JAXBElement<TimeInfo> value) {
        this.startTime = value;
    }

    /**
     * Gets the value of the endTime property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TimeInfo }{@code >}
     *     
     */
    public JAXBElement<TimeInfo> getEndTime() {
        return endTime;
    }

    /**
     * Sets the value of the endTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TimeInfo }{@code >}
     *     
     */
    public void setEndTime(JAXBElement<TimeInfo> value) {
        this.endTime = value;
    }

    /**
     * Gets the value of the interval property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getInterval() {
        return interval;
    }

    /**
     * Sets the value of the interval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setInterval(Integer value) {
        this.interval = value;
    }

    /**
     * Gets the value of the probes property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getProbes() {
        return probes;
    }

    /**
     * Sets the value of the probes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setProbes(Integer value) {
        this.probes = value;
    }

    /**
     * Gets the value of the ipAddressRefresh property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIpAddressRefresh() {
        return ipAddressRefresh;
    }

    /**
     * Sets the value of the ipAddressRefresh property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIpAddressRefresh(Integer value) {
        this.ipAddressRefresh = value;
    }

    /**
     * Gets the value of the ipAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the value of the ipAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpAddress(String value) {
        this.ipAddress = value;
    }

    /**
     * Gets the value of the port property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Sets the value of the port property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPort(Integer value) {
        this.port = value;
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

}
