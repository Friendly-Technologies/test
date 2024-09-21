
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for callApiTask complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="callApiTask"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ftacs.com/}updateTaskWS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="apiUrl" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="apiRequest" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="apiMethodName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "callApiTask", propOrder = {
    "apiUrl",
    "apiRequest",
    "apiMethodName"
})
public class CallApiTask
    extends UpdateTaskWS
{

    @XmlElement(required = true)
    protected String apiUrl;
    @XmlElement(required = true)
    protected String apiRequest;
    protected String apiMethodName;

    /**
     * Gets the value of the apiUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApiUrl() {
        return apiUrl;
    }

    /**
     * Sets the value of the apiUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApiUrl(String value) {
        this.apiUrl = value;
    }

    /**
     * Gets the value of the apiRequest property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApiRequest() {
        return apiRequest;
    }

    /**
     * Sets the value of the apiRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApiRequest(String value) {
        this.apiRequest = value;
    }

    /**
     * Gets the value of the apiMethodName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApiMethodName() {
        return apiMethodName;
    }

    /**
     * Sets the value of the apiMethodName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApiMethodName(String value) {
        this.apiMethodName = value;
    }

}
