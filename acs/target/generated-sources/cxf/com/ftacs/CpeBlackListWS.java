
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cpeBlackListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cpeBlackListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CPE" type="{http://ftacs.com/}cpeBlackListDataWS" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cpeBlackListWS", propOrder = {
    "cpe"
})
public class CpeBlackListWS {

    @XmlElement(name = "CPE", required = true)
    protected List<CpeBlackListDataWS> cpe;

    /**
     * Gets the value of the cpe property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cpe property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCPE().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CpeBlackListDataWS }
     * 
     * 
     */
    public List<CpeBlackListDataWS> getCPE() {
        if (cpe == null) {
            cpe = new ArrayList<CpeBlackListDataWS>();
        }
        return this.cpe;
    }

}
