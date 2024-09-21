
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteEvent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ids" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteEvent", propOrder = {
    "ids"
})
public class DeleteEvent {

    protected IntegerArrayWS ids;

    /**
     * Gets the value of the ids property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getIds() {
        return ids;
    }

    /**
     * Sets the value of the ids property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setIds(IntegerArrayWS value) {
        this.ids = value;
    }

}
