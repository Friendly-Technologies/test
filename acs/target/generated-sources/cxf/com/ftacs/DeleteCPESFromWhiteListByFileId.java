
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteCPESFromWhiteListByFileId complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteCPESFromWhiteListByFileId"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cpeWhiteListId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteCPESFromWhiteListByFileId", propOrder = {
    "cpeWhiteListId"
})
public class DeleteCPESFromWhiteListByFileId {

    protected Integer cpeWhiteListId;

    /**
     * Gets the value of the cpeWhiteListId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCpeWhiteListId() {
        return cpeWhiteListId;
    }

    /**
     * Sets the value of the cpeWhiteListId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCpeWhiteListId(Integer value) {
        this.cpeWhiteListId = value;
    }

}
