package com.friendly.commons.models.device.response;

import com.friendly.commons.models.device.file.FileTypeFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
public class FileTypeFiltersResponse {
    List<FileTypeFilter> items;
}
