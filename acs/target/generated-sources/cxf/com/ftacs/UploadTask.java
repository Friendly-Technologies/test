
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for uploadTask complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="uploadTask"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ftacs.com/}updateTaskWS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="file" type="{http://ftacs.com/}cpeUploadFile"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uploadTask", propOrder = {
    "file"
})
@XmlSeeAlso({
    BackupCpeConfigTask.class
})
public class UploadTask
    extends UpdateTaskWS
{

    @XmlElement(required = true)
    protected CpeUploadFile file;

    /**
     * Gets the value of the file property.
     * 
     * @return
     *     possible object is
     *     {@link CpeUploadFile }
     *     
     */
    public CpeUploadFile getFile() {
        return file;
    }

    /**
     * Sets the value of the file property.
     * 
     * @param value
     *     allowed object is
     *     {@link CpeUploadFile }
     *     
     */
    public void setFile(CpeUploadFile value) {
        this.file = value;
    }

}
