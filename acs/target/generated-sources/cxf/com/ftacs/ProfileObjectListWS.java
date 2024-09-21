
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for profileObjectListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profileObjectListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ProfileObject" type="{http://ftacs.com/}profileObjectWS" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileObjectListWS", propOrder = {
    "profileObject"
})
public class ProfileObjectListWS {

    @XmlElement(name = "ProfileObject", required = true)
    protected List<ProfileObjectWS> profileObject;

    /**
     * Gets the value of the profileObject property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the profileObject property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProfileObject().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProfileObjectWS }
     * 
     * 
     */
    public List<ProfileObjectWS> getProfileObject() {
        if (profileObject == null) {
            profileObject = new ArrayList<ProfileObjectWS>();
        }
        return this.profileObject;
    }

}
