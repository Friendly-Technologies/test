
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for installOpListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="installOpListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="installOperation" type="{http://ftacs.com/}installOpStructWS" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "installOpListWS", propOrder = {
    "installOperation"
})
public class InstallOpListWS {

    @XmlElement(required = true)
    protected List<InstallOpStructWS> installOperation;

    /**
     * Gets the value of the installOperation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the installOperation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInstallOperation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InstallOpStructWS }
     * 
     * 
     */
    public List<InstallOpStructWS> getInstallOperation() {
        if (installOperation == null) {
            installOperation = new ArrayList<InstallOpStructWS>();
        }
        return this.installOperation;
    }

}
