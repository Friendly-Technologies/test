
package com.ftacs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for eventReceiverUrlWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eventReceiverUrlWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="url" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="useFtacsNs" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="authProps" type="{http://ftacs.com/}eventReceiverUrlAuthWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eventReceiverUrlWS", propOrder = {
    "id",
    "url",
    "useFtacsNs",
    "authProps"
})
public class EventReceiverUrlWS {

    protected Integer id;
    protected String url;
    @XmlElementRef(name = "useFtacsNs", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> useFtacsNs;
    @XmlElementRef(name = "authProps", type = JAXBElement.class, required = false)
    protected JAXBElement<EventReceiverUrlAuthWS> authProps;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setId(Integer value) {
        this.id = value;
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
     * Gets the value of the useFtacsNs property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getUseFtacsNs() {
        return useFtacsNs;
    }

    /**
     * Sets the value of the useFtacsNs property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setUseFtacsNs(JAXBElement<Boolean> value) {
        this.useFtacsNs = value;
    }

    /**
     * Gets the value of the authProps property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link EventReceiverUrlAuthWS }{@code >}
     *     
     */
    public JAXBElement<EventReceiverUrlAuthWS> getAuthProps() {
        return authProps;
    }

    /**
     * Sets the value of the authProps property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link EventReceiverUrlAuthWS }{@code >}
     *     
     */
    public void setAuthProps(JAXBElement<EventReceiverUrlAuthWS> value) {
        this.authProps = value;
    }

}
