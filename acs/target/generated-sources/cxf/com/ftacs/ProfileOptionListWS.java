
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for profileOptionListWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="profileOptionListWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="profileOption" type="{http://ftacs.com/}profileOptionWS" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "profileOptionListWS", propOrder = {
    "profileOption"
})
public class ProfileOptionListWS {

    @XmlElement(required = true)
    protected List<ProfileOptionWS> profileOption;

    /**
     * Gets the value of the profileOption property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the profileOption property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProfileOption().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ProfileOptionWS }
     * 
     * 
     */
    public List<ProfileOptionWS> getProfileOption() {
        if (profileOption == null) {
            profileOption = new ArrayList<ProfileOptionWS>();
        }
        return this.profileOption;
    }

}
