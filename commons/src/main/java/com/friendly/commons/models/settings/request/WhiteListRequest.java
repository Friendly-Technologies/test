package com.friendly.commons.models.settings.request;

import com.friendly.commons.models.settings.acs.whitelist.WhiteListSerialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@Builder
public class WhiteListRequest {
    String description;
    WhiteListSerialType type;
}
