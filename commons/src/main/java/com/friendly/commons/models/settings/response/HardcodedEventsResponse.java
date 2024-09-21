package com.friendly.commons.models.settings.response;

import com.friendly.commons.models.settings.acs.events.HardcodedEventItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class HardcodedEventsResponse {
//    HardcodedEventsGeneralConfig generalConfig;
    List<HardcodedEventItem> items;
}