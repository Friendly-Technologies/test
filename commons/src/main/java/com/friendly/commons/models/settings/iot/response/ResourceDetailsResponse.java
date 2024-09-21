package com.friendly.commons.models.settings.iot.response;

import com.friendly.commons.models.settings.resource.ResourceDetails;
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
public class ResourceDetailsResponse {
    List<ResourceDetails> items;
}
