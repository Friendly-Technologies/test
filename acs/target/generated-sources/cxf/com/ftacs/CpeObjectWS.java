
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cpeObjectWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cpeObjectWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CPEObjectParamList" type="{http://ftacs.com/}cpeObjectParamListWS" minOccurs="0"/&gt;
 *         &lt;element name="objectName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="reprovision" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cpeObjectWS", propOrder = {
    "cpeObjectParamList",
    "objectName",
    "reprovision"
})
public class CpeObjectWS {

    @XmlElement(name = "CPEObjectParamList")
    protected CpeObjectParamListWS cpeObjectParamList;
    protected String objectName;
    protected boolean reprovision;

    /**
     * Gets the value of the cpeObjectParamList property.
     * 
     * @return
     *     possible object is
     *     {@link CpeObjectParamListWS }
     *     
     */
    public CpeObjectParamListWS getCPEObjectParamList() {
        return cpeObjectParamList;
    }

    /**
     * Sets the value of the cpeObjectParamList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpeObjectParamListWS }
     *     
     */
    public void setCPEObjectParamList(CpeObjectParamListWS value) {
        this.cpeObjectParamList = value;
    }

    /**
     * Gets the value of the objectName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Sets the value of the objectName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectName(String value) {
        this.objectName = value;
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

}
