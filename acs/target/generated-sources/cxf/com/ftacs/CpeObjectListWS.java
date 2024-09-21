
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cpeObjectListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cpeObjectListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cpeObject" type="{http://ftacs.com/}cpeObjectWS" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cpeObjectListWS", propOrder = {
    "cpeObject"
})
public class CpeObjectListWS {

    @XmlElement(nillable = true)
    protected List<CpeObjectWS> cpeObject;

    /**
     * Gets the value of the cpeObject property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cpeObject property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCpeObject().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CpeObjectWS }
     * 
     * 
     */
    public List<CpeObjectWS> getCpeObject() {
        if (cpeObject == null) {
            cpeObject = new ArrayList<CpeObjectWS>();
        }
        return this.cpeObject;
    }

}
