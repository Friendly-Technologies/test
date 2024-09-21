package com.friendly.commons.models.settings.response;

import com.friendly.commons.models.settings.config.AbstractConfigItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AbstractConfigItemsResponse {
    Set<AbstractConfigItem> items;
}
