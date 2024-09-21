
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cpeParamAttribListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cpeParamAttribListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CPEParamAttrib" type="{http://ftacs.com/}cpeParamAttribWS" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cpeParamAttribListWS", propOrder = {
    "cpeParamAttrib"
})
public class CpeParamAttribListWS {

    @XmlElement(name = "CPEParamAttrib", required = true)
    protected List<CpeParamAttribWS> cpeParamAttrib;

    /**
     * Gets the value of the cpeParamAttrib property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cpeParamAttrib property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCPEParamAttrib().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CpeParamAttribWS }
     * 
     * 
     */
    public List<CpeParamAttribWS> getCPEParamAttrib() {
        if (cpeParamAttrib == null) {
            cpeParamAttrib = new ArrayList<CpeParamAttribWS>();
        }
        return this.cpeParamAttrib;
    }

}
