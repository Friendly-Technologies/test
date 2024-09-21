package com.friendly.commons.models.settings.response;

import com.friendly.commons.models.settings.SnmpVersionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SnmpServerResponse {
    String host;
    String port;
    String community;
    SnmpVersionType version;
}
