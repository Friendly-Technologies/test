package com.friendly.commons.models.settings.acs.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class HardcodedEventUrlItem {
    Integer id;
    String url;
    Boolean useFtacsNs;
}