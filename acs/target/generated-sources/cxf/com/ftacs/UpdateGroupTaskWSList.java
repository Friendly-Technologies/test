
package com.ftacs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for updateGroupTaskWSList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="updateGroupTaskWSList"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element name="getTask" type="{http://ftacs.com/}getTask"/&gt;
 *           &lt;element name="setValueTask" type="{http://ftacs.com/}setValueTask"/&gt;
 *           &lt;element name="setAttributeTask" type="{http://ftacs.com/}setAttributesTask"/&gt;
 *           &lt;element name="downloadTask" type="{http://ftacs.com/}downloadTask"/&gt;
 *           &lt;element name="rebootTask" type="{http://ftacs.com/}rebootTask"/&gt;
 *           &lt;element name="factoryResetTask" type="{http://ftacs.com/}factoryResetTask"/&gt;
 *           &lt;element name="rpcMethodTask" type="{http://ftacs.com/}rpcMethodTask"/&gt;
 *           &lt;element name="uploadTask" type="{http://ftacs.com/}uploadTask"/&gt;
 *           &lt;element name="reprovisionTask" type="{http://ftacs.com/}reprovisionTask"/&gt;
 *           &lt;element name="backupCpeConfigTask" type="{http://ftacs.com/}backupCpeConfigTask"/&gt;
 *           &lt;element name="restoreCpeConfigTask" type="{http://ftacs.com/}restoreCpeConfigTask"/&gt;
 *           &lt;element name="changeDUStateTask" type="{http://ftacs.com/}changeDUSateTask"/&gt;
 *           &lt;element name="cpeMethodTask" type="{http://ftacs.com/}cpeMethodTask"/&gt;
 *           &lt;element name="diagnosticTaskWS" type="{http://ftacs.com/}diagnosticTaskWS"/&gt;
 *           &lt;element name="callApiTaskWS" type="{http://ftacs.com/}callApiTask"/&gt;
 *           &lt;element name="addToProvisionTaskWS" type="{http://ftacs.com/}AddToProvisionTask"/&gt;
 *           &lt;element name="udpPingTaskWS" type="{http://ftacs.com/}udpPingTask"/&gt;
 *           &lt;element name="fccTaskWS" type="{http://ftacs.com/}fccTaskWS"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "updateGroupTaskWSList", propOrder = {
    "getTaskOrSetValueTaskOrSetAttributeTask"
})
public class UpdateGroupTaskWSList {

    @XmlElements({
        @XmlElement(name = "getTask", type = GetTask.class),
        @XmlElement(name = "setValueTask", type = SetValueTask.class),
        @XmlElement(name = "setAttributeTask", type = SetAttributesTask.class),
        @XmlElement(name = "downloadTask", type = DownloadTask.class),
        @XmlElement(name = "rebootTask", type = RebootTask.class),
        @XmlElement(name = "factoryResetTask", type = FactoryResetTask.class),
        @XmlElement(name = "rpcMethodTask", type = RpcMethodTask.class),
        @XmlElement(name = "uploadTask", type = UploadTask.class),
        @XmlElement(name = "reprovisionTask", type = ReprovisionTask.class),
        @XmlElement(name = "backupCpeConfigTask", type = BackupCpeConfigTask.class),
        @XmlElement(name = "restoreCpeConfigTask", type = RestoreCpeConfigTask.class),
        @XmlElement(name = "changeDUStateTask", type = ChangeDUSateTask.class),
        @XmlElement(name = "cpeMethodTask", type = CpeMethodTask.class),
        @XmlElement(name = "diagnosticTaskWS", type = DiagnosticTaskWS.class),
        @XmlElement(name = "callApiTaskWS", type = CallApiTask.class),
        @XmlElement(name = "addToProvisionTaskWS", type = AddToProvisionTask.class),
        @XmlElement(name = "udpPingTaskWS", type = UdpPingTask.class),
        @XmlElement(name = "fccTaskWS", type = FccTaskWS.class)
    })
    protected List<UpdateTaskWS> getTaskOrSetValueTaskOrSetAttributeTask;

    /**
     * Gets the value of the getTaskOrSetValueTaskOrSetAttributeTask property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the getTaskOrSetValueTaskOrSetAttributeTask property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGetTaskOrSetValueTaskOrSetAttributeTask().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AddToProvisionTask }
     * {@link BackupCpeConfigTask }
     * {@link CallApiTask }
     * {@link ChangeDUSateTask }
     * {@link CpeMethodTask }
     * {@link DiagnosticTaskWS }
     * {@link DownloadTask }
     * {@link FactoryResetTask }
     * {@link FccTaskWS }
     * {@link GetTask }
     * {@link RebootTask }
     * {@link ReprovisionTask }
     * {@link RestoreCpeConfigTask }
     * {@link RpcMethodTask }
     * {@link SetAttributesTask }
     * {@link SetValueTask }
     * {@link UdpPingTask }
     * {@link UploadTask }
     * 
     * 
     */
    public List<UpdateTaskWS> getGetTaskOrSetValueTaskOrSetAttributeTask() {
        if (getTaskOrSetValueTaskOrSetAttributeTask == null) {
            getTaskOrSetValueTaskOrSetAttributeTask = new ArrayList<UpdateTaskWS>();
        }
        return this.getTaskOrSetValueTaskOrSetAttributeTask;
    }

}
