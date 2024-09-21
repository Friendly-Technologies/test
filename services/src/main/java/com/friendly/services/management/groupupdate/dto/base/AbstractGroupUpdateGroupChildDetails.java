package com.friendly.services.management.groupupdate.dto.base;

import com.friendly.services.management.groupupdate.dto.enums.SourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AbstractGroupUpdateGroupChildDetails implements Serializable {
    private Integer id;
    private String manufacturer;
    private String model;
    private SourceType sourceType;
    private Integer conditionId;
}