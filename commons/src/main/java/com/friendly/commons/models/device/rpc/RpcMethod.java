package com.friendly.commons.models.device.rpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that represents API version of Custom RPC
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@EqualsAndHashCode
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RpcMethod implements Serializable {

    private String method;
    private String request;

}
