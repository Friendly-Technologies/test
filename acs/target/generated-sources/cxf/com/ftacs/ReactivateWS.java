
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for reactivateWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reactivateWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="reactivateExpr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="reactivateOnFailed" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="reactivateRepeats" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="from" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="to" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reactivateWS", propOrder = {
    "reactivateExpr",
    "reactivateOnFailed",
    "reactivateRepeats",
    "from",
    "to"
})
public class ReactivateWS {

    protected String reactivateExpr;
    protected boolean reactivateOnFailed;
    protected Integer reactivateRepeats;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar from;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar to;

    /**
     * Gets the value of the reactivateExpr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReactivateExpr() {
        return reactivateExpr;
    }

    /**
     * Sets the value of the reactivateExpr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReactivateExpr(String value) {
        this.reactivateExpr = value;
    }

    /**
     * Gets the value of the reactivateOnFailed property.
     * 
     */
    public boolean isReactivateOnFailed() {
        return reactivateOnFailed;
    }

    /**
     * Sets the value of the reactivateOnFailed property.
     * 
     */
    public void setReactivateOnFailed(boolean value) {
        this.reactivateOnFailed = value;
    }

    /**
     * Gets the value of the reactivateRepeats property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getReactivateRepeats() {
        return reactivateRepeats;
    }

    /**
     * Sets the value of the reactivateRepeats property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setReactivateRepeats(Integer value) {
        this.reactivateRepeats = value;
    }

    /**
     * Gets the value of the from property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFrom(XMLGregorianCalendar value) {
        this.from = value;
    }

    /**
     * Gets the value of the to property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTo() {
        return to;
    }

    /**
     * Sets the value of the to property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTo(XMLGregorianCalendar value) {
        this.to = value;
    }

}
