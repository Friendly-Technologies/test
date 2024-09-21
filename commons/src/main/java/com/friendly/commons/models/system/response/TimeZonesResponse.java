package com.friendly.commons.models.system.response;

import com.friendly.commons.models.system.TimeZone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
public class TimeZonesResponse {
    Set<TimeZone> items;
}
