
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for custDeviceFieldListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="custDeviceFieldListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="custDeviceField" type="{http://ftacs.com/}custDeviceFieldWS" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "custDeviceFieldListWS", propOrder = {
    "custDeviceField"
})
public class CustDeviceFieldListWS {

    @XmlElement(required = true)
    protected List<CustDeviceFieldWS> custDeviceField;

    /**
     * Gets the value of the custDeviceField property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the custDeviceField property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCustDeviceField().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustDeviceFieldWS }
     * 
     * 
     */
    public List<CustDeviceFieldWS> getCustDeviceField() {
        if (custDeviceField == null) {
            custDeviceField = new ArrayList<CustDeviceFieldWS>();
        }
        return this.custDeviceField;
    }

}
