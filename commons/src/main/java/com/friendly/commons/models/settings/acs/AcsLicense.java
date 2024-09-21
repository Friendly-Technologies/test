package com.friendly.commons.models.settings.acs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AcsLicense implements Serializable {

    private String created;
    private Instant createdIso;

    private String cpeAdminUsers;
    private String csrUsers;
    private String customerName;

    private String timeLimit;
    private Instant expireDateIso;

    private String limitDevices;
    private String limitTR069;
    private String limitUSP;
    private String limitLWM2M;
    private String limitMQTT;
    private String type;

    @JsonIgnore
    private Long useDevices;
    @JsonIgnore
    private Long useTR069;
    @JsonIgnore
    private Long useUSP;
    @JsonIgnore
    private Long useLWM2M;
    @JsonIgnore
    private Long useMQTT;

}
