
package com.ftacs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for rpcMethodTask complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rpcMethodTask"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://ftacs.com/}updateTaskWS"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="rpcMethod" type="{http://ftacs.com/}rpcMethodWS"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rpcMethodTask", propOrder = {
    "rpcMethod"
})
public class RpcMethodTask
    extends UpdateTaskWS
{

    @XmlElement(required = true)
    protected RpcMethodWS rpcMethod;

    /**
     * Gets the value of the rpcMethod property.
     * 
     * @return
     *     possible object is
     *     {@link RpcMethodWS }
     *     
     */
    public RpcMethodWS getRpcMethod() {
        return rpcMethod;
    }

    /**
     * Sets the value of the rpcMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link RpcMethodWS }
     *     
     */
    public void setRpcMethod(RpcMethodWS value) {
        this.rpcMethod = value;
    }

}
