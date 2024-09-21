package com.friendly.commons.models.settings.acs.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class HardcodedEventsGeneralConfig implements Serializable {
    String webServiceUrl;
    String femsUrl;
    Integer sendTo;
    Integer attempts;
    Integer interval;
    Integer timeout;
    Boolean send;
    String protocol;
}