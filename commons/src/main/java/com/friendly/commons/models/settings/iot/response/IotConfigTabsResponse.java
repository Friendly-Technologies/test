package com.friendly.commons.models.settings.iot.response;

import com.friendly.commons.models.settings.acs.IotConfigTab;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class IotConfigTabsResponse {
    List<IotConfigTab> items;
}
