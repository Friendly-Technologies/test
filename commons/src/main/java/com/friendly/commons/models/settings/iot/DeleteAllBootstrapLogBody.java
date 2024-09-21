package com.friendly.commons.models.settings.iot;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteAllBootstrapLogBody {
    String searchParam;
    boolean searchExact;
}
