
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for downloadTask complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="downloadTask"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ftacs.com/}updateTaskWS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="file" type="{http://ftacs.com/}cpeFileWS"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "downloadTask", propOrder = {
    "file"
})
@XmlSeeAlso({
    RestoreCpeConfigTask.class
})
public class DownloadTask
    extends UpdateTaskWS
{

    @XmlElement(required = true)
    protected CpeFileWS file;

    /**
     * Gets the value of the file property.
     * 
     * @return
     *     possible object is
     *     {@link CpeFileWS }
     *     
     */
    public CpeFileWS getFile() {
        return file;
    }

    /**
     * Sets the value of the file property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpeFileWS }
     *     
     */
    public void setFile(CpeFileWS value) {
        this.file = value;
    }

}
