package com.friendly.services.management.action.dto.response;

import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class ActionMethods {
    private String description;
    private ActionTypeEnum type;
    private List<ActionMethodDetails> types;
}



/*

	IF type is not null => set type in PUT call
	IF type is null => look for types sub array

{
  [
        {
            type: GET_TASK
            description: "Get parameter"
        },
        {
            description: "Action"
            types:
                [
                    {
                        type: REBOOT_TASK,
                        description: "Reboot"
                    },
                    {
                        type: CPE_METHOD_TASK,
                        details:
                            [
                                {
                                    name: "Device.Factory reset"
                                    instances: [] // list of supported instances should be taken from device_template
                                }
                            ]
                    },
                    {
                        type: RPC_METHOD_TASK,
                        details:
                            [
                              com.friendly.commons.models.device.rpc.RpcMethod

                            ]
                    }

                ]
        }
  ]
}





getTask,
setValueTask,
setAttributeTask
downloadTask
    rebootTask
    factoryResetTask
    rpcMethodTask
uploadTask
    reprovisionTask
backupCpeConfigTask
restoreCpeConfigTask
changeDUStateTask
    cpeMethodTask
diagnosticTaskWS
callApiTaskWS
addToProvisionTaskWS
udpPingTaskWS
*/