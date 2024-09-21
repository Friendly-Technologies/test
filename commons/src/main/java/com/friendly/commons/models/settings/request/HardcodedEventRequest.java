package com.friendly.commons.models.settings.request;

import com.friendly.commons.models.settings.acs.events.HardcodedEventItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@Builder
public class HardcodedEventRequest {
    List<HardcodedEventItem> items;
}
