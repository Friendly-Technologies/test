package com.friendly.commons.models.settings.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class WhiteListIpRequest {
    String manufacturer;
    String model;
    String ipRange;
    boolean isOnlyCreated;
}
