
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cpeParamListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cpeParamListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CPEParam" type="{http://ftacs.com/}cpeParamWS" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cpeParamListWS", propOrder = {
    "cpeParam"
})
public class CpeParamListWS {

    @XmlElement(name = "CPEParam", required = true)
    protected List<CpeParamWS> cpeParam;

    /**
     * Gets the value of the cpeParam property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cpeParam property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCPEParam().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CpeParamWS }
     * 
     * 
     */
    public List<CpeParamWS> getCPEParam() {
        if (cpeParam == null) {
            cpeParam = new ArrayList<CpeParamWS>();
        }
        return this.cpeParam;
    }

}
