
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for addCPEDiag complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="addCPEDiag"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cpeList" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *         &lt;element name="cpeDiag" type="{http://ftacs.com/}cpeDiagnosticWS" minOccurs="0"/&gt;
 *         &lt;element name="priority" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="push" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="user" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addCPEDiag", propOrder = {
    "cpeList",
    "cpeDiag",
    "priority",
    "push",
    "user"
})
public class AddCPEDiag {

    protected IntegerArrayWS cpeList;
    protected CpeDiagnosticWS cpeDiag;
    protected Integer priority;
    protected boolean push;
    protected String user;

    /**
     * Gets the value of the cpeList property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getCpeList() {
        return cpeList;
    }

    /**
     * Sets the value of the cpeList property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setCpeList(IntegerArrayWS value) {
        this.cpeList = value;
    }

    /**
     * Gets the value of the cpeDiag property.
     * 
     * @return
     *     possible object is
     *     {@link CpeDiagnosticWS }
     *     
     */
    public CpeDiagnosticWS getCpeDiag() {
        return cpeDiag;
    }

    /**
     * Sets the value of the cpeDiag property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpeDiagnosticWS }
     *     
     */
    public void setCpeDiag(CpeDiagnosticWS value) {
        this.cpeDiag = value;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPriority(Integer value) {
        this.priority = value;
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
     * Gets the value of the user property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the value of the user property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

}
