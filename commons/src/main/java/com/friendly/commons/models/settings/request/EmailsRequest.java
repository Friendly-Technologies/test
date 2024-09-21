package com.friendly.commons.models.settings.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@FieldDefaults(level = PRIVATE)
public class EmailsRequest {
    @NonNull
    Set<String> emails;
}
