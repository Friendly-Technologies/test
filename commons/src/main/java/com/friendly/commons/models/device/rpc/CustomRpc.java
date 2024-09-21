package com.friendly.commons.models.device.rpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

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
public class CustomRpc implements Serializable {

    private Long taskId;
    private String state;
    private Instant createdIso;
    private String created;
    private String creator;
    private String application;
    private String method;
    private String request;
    private String response;
    private String completed;
    private Instant completedIso;
}
