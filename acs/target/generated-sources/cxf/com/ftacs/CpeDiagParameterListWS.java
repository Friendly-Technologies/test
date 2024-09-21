
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cpeDiagParameterListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cpeDiagParameterListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CPEDiagParameter" type="{http://ftacs.com/}cpeDiagParameterWS" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cpeDiagParameterListWS", propOrder = {
    "cpeDiagParameter"
})
public class CpeDiagParameterListWS {

    @XmlElement(name = "CPEDiagParameter")
    protected List<CpeDiagParameterWS> cpeDiagParameter;

    /**
     * Gets the value of the cpeDiagParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cpeDiagParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCPEDiagParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CpeDiagParameterWS }
     * 
     * 
     */
    public List<CpeDiagParameterWS> getCPEDiagParameter() {
        if (cpeDiagParameter == null) {
            cpeDiagParameter = new ArrayList<CpeDiagParameterWS>();
        }
        return this.cpeDiagParameter;
    }

}
