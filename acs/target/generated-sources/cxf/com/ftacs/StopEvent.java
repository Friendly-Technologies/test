
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for stopEvent complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="stopEvent"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="parentEventId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="needReset" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "stopEvent", propOrder = {
    "parentEventId",
    "needReset"
})
public class StopEvent {

    protected Integer parentEventId;
    protected boolean needReset;

    /**
     * Gets the value of the parentEventId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getParentEventId() {
        return parentEventId;
    }

    /**
     * Sets the value of the parentEventId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setParentEventId(Integer value) {
        this.parentEventId = value;
    }

    /**
     * Gets the value of the needReset property.
     * 
     */
    public boolean isNeedReset() {
        return needReset;
    }

    /**
     * Sets the value of the needReset property.
     * 
     */
    public void setNeedReset(boolean value) {
        this.needReset = value;
    }

}
