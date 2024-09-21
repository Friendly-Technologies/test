package com.friendly.services.management.groupupdate.dto;

import com.friendly.services.management.groupupdate.dto.enums.SourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateFilters {
    private String manufacturer;
    private String model;
    private Integer conditionId;
    SourceType sourceType;
    private List<Integer> pageNumbers;
    private Integer pageSize;
    private String searchParam;
    private Boolean searchExact;
}
