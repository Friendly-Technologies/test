
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for getEntryWS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getEntryWS"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *       &lt;attribute name="names" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="values" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="attributes" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getEntryWS", propOrder = {
    "value"
})
public class GetEntryWS {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "names", required = true)
    protected boolean names;
    @XmlAttribute(name = "values", required = true)
    protected boolean values;
    @XmlAttribute(name = "attributes", required = true)
    protected boolean attributes;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the names property.
     * 
     */
    public boolean isNames() {
        return names;
    }

    /**
     * Sets the value of the names property.
     * 
     */
    public void setNames(boolean value) {
        this.names = value;
    }

    /**
     * Gets the value of the values property.
     * 
     */
    public boolean isValues() {
        return values;
    }

    /**
     * Sets the value of the values property.
     * 
     */
    public void setValues(boolean value) {
        this.values = value;
    }

    /**
     * Gets the value of the attributes property.
     * 
     */
    public boolean isAttributes() {
        return attributes;
    }

    /**
     * Sets the value of the attributes property.
     * 
     */
    public void setAttributes(boolean value) {
        this.attributes = value;
    }

}
