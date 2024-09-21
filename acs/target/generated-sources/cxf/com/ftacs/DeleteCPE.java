
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for deleteCPE complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="deleteCPE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cpeList" type="{http://ftacs.com/}integerArrayWS" minOccurs="0"/&gt;
 *         &lt;element name="deleteFromCpeSerial" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="totalDelete" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "deleteCPE", propOrder = {
    "cpeList",
    "deleteFromCpeSerial",
    "totalDelete"
})
public class DeleteCPE {

    protected IntegerArrayWS cpeList;
    protected boolean deleteFromCpeSerial;
    protected boolean totalDelete;

    /**
     * Gets the value of the cpeList property.
     * 
     * @return
     *     possible object is
     *     {@link IntegerArrayWS }
     *     
     */
    public IntegerArrayWS getCpeList() {
        return cpeList;
    }

    /**
     * Sets the value of the cpeList property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntegerArrayWS }
     *     
     */
    public void setCpeList(IntegerArrayWS value) {
        this.cpeList = value;
    }

    /**
     * Gets the value of the deleteFromCpeSerial property.
     * 
     */
    public boolean isDeleteFromCpeSerial() {
        return deleteFromCpeSerial;
    }

    /**
     * Sets the value of the deleteFromCpeSerial property.
     * 
     */
    public void setDeleteFromCpeSerial(boolean value) {
        this.deleteFromCpeSerial = value;
    }

    /**
     * Gets the value of the totalDelete property.
     * 
     */
    public boolean isTotalDelete() {
        return totalDelete;
    }

    /**
     * Sets the value of the totalDelete property.
     * 
     */
    public void setTotalDelete(boolean value) {
        this.totalDelete = value;
    }

}
