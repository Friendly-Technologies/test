
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for addRetrieveMethod complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="addRetrieveMethod"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="groups" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addRetrieveMethod", propOrder = {
    "groups"
})
public class AddRetrieveMethod {

    protected IntegerArrayWS groups;

    /**
     * Gets the value of the groups property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getGroups() {
        return groups;
    }

    /**
     * Sets the value of the groups property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setGroups(IntegerArrayWS value) {
        this.groups = value;
    }

}
