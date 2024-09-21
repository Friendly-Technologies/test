
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for rpcMethodWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rpcMethodWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="methodName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="reprovision" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="requestMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rpcMethodWS", propOrder = {
    "methodName",
    "reprovision",
    "requestMessage"
})
public class RpcMethodWS {

    protected String methodName;
    protected boolean reprovision;
    protected String requestMessage;

    /**
     * Gets the value of the methodName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Sets the value of the methodName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMethodName(String value) {
        this.methodName = value;
    }

    /**
     * Gets the value of the reprovision property.
     * 
     */
    public boolean isReprovision() {
        return reprovision;
    }

    /**
     * Sets the value of the reprovision property.
     * 
     */
    public void setReprovision(boolean value) {
        this.reprovision = value;
    }

    /**
     * Gets the value of the requestMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestMessage() {
        return requestMessage;
    }

    /**
     * Sets the value of the requestMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestMessage(String value) {
        this.requestMessage = value;
    }

}
