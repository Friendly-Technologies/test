package com.friendly.commons.models.settings.response;

import com.friendly.commons.models.settings.config.AbstractConfigItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class AbstractConfigItemsListResponse {
    List<AbstractConfigItem> items;
}