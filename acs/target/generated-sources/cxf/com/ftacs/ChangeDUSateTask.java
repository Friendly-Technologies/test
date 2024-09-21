
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for changeDUSateTask complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="changeDUSateTask"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ftacs.com/}updateTaskWS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="installOperations" type="{http://ftacs.com/}installOpListWS" minOccurs="0"/&gt;
 *         &lt;element name="updateOperations" type="{http://ftacs.com/}updateOpListWS" minOccurs="0"/&gt;
 *         &lt;element name="unInstallOperations" type="{http://ftacs.com/}unInstallOpListWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "changeDUSateTask", propOrder = {
    "installOperations",
    "updateOperations",
    "unInstallOperations"
})
public class ChangeDUSateTask
    extends UpdateTaskWS
{

    protected InstallOpListWS installOperations;
    protected UpdateOpListWS updateOperations;
    protected UnInstallOpListWS unInstallOperations;

    /**
     * Gets the value of the installOperations property.
     * 
     * @return
     *     possible object is
     *     {@link InstallOpListWS }
     *     
     */
    public InstallOpListWS getInstallOperations() {
        return installOperations;
    }

    /**
     * Sets the value of the installOperations property.
     * 
     * @param value
     *     allowed object is
     *     {@link InstallOpListWS }
     *     
     */
    public void setInstallOperations(InstallOpListWS value) {
        this.installOperations = value;
    }

    /**
     * Gets the value of the updateOperations property.
     * 
     * @return
     *     possible object is
     *     {@link UpdateOpListWS }
     *     
     */
    public UpdateOpListWS getUpdateOperations() {
        return updateOperations;
    }

    /**
     * Sets the value of the updateOperations property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpdateOpListWS }
     *     
     */
    public void setUpdateOperations(UpdateOpListWS value) {
        this.updateOperations = value;
    }

    /**
     * Gets the value of the unInstallOperations property.
     * 
     * @return
     *     possible object is
     *     {@link UnInstallOpListWS }
     *     
     */
    public UnInstallOpListWS getUnInstallOperations() {
        return unInstallOperations;
    }

    /**
     * Sets the value of the unInstallOperations property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnInstallOpListWS }
     *     
     */
    public void setUnInstallOperations(UnInstallOpListWS value) {
        this.unInstallOperations = value;
    }

}
