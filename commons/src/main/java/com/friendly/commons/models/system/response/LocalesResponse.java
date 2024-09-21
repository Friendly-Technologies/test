package com.friendly.commons.models.system.response;

import com.friendly.commons.models.user.Locale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
public class LocalesResponse {
    List<Locale> items;
}
