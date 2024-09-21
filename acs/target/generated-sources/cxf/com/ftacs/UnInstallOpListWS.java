
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for unInstallOpListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="unInstallOpListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="unInstallOperation" type="{http://ftacs.com/}unInstallOpStructWS" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "unInstallOpListWS", propOrder = {
    "unInstallOperation"
})
public class UnInstallOpListWS {

    @XmlElement(required = true)
    protected List<UnInstallOpStructWS> unInstallOperation;

    /**
     * Gets the value of the unInstallOperation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the unInstallOperation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUnInstallOperation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UnInstallOpStructWS }
     * 
     * 
     */
    public List<UnInstallOpStructWS> getUnInstallOperation() {
        if (unInstallOperation == null) {
            unInstallOperation = new ArrayList<UnInstallOpStructWS>();
        }
        return this.unInstallOperation;
    }

}
