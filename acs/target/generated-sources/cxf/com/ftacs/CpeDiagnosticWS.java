
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cpeDiagnosticWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cpeDiagnosticWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cpeDiagGetParameters" type="{http://ftacs.com/}cpeDiagParameterListWS" minOccurs="0"/&gt;
 *         &lt;element name="cpeDiagName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cpeDiagSetParameters" type="{http://ftacs.com/}cpeDiagParameterListWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cpeDiagnosticWS", propOrder = {
    "cpeDiagGetParameters",
    "cpeDiagName",
    "cpeDiagSetParameters"
})
public class CpeDiagnosticWS {

    protected CpeDiagParameterListWS cpeDiagGetParameters;
    protected String cpeDiagName;
    protected CpeDiagParameterListWS cpeDiagSetParameters;

    /**
     * Gets the value of the cpeDiagGetParameters property.
     * 
     * @return
     *     possible object is
     *     {@link CpeDiagParameterListWS }
     *     
     */
    public CpeDiagParameterListWS getCpeDiagGetParameters() {
        return cpeDiagGetParameters;
    }

    /**
     * Sets the value of the cpeDiagGetParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpeDiagParameterListWS }
     *     
     */
    public void setCpeDiagGetParameters(CpeDiagParameterListWS value) {
        this.cpeDiagGetParameters = value;
    }

    /**
     * Gets the value of the cpeDiagName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCpeDiagName() {
        return cpeDiagName;
    }

    /**
     * Sets the value of the cpeDiagName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCpeDiagName(String value) {
        this.cpeDiagName = value;
    }

    /**
     * Gets the value of the cpeDiagSetParameters property.
     * 
     * @return
     *     possible object is
     *     {@link CpeDiagParameterListWS }
     *     
     */
    public CpeDiagParameterListWS getCpeDiagSetParameters() {
        return cpeDiagSetParameters;
    }

    /**
     * Sets the value of the cpeDiagSetParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpeDiagParameterListWS }
     *     
     */
    public void setCpeDiagSetParameters(CpeDiagParameterListWS value) {
        this.cpeDiagSetParameters = value;
    }

}
