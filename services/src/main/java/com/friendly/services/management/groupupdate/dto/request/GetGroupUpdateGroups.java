package com.friendly.services.management.groupupdate.dto.request;

import com.friendly.commons.models.FieldSort;
import com.friendly.services.management.groupupdate.dto.enums.GroupUpdateStateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GetGroupUpdateGroups implements Serializable {
    private String manufacturer;
    private String model;
    private GroupUpdateStateType state;
    private List<Integer> pageNumbers;
    private Integer pageSize;
    private List<FieldSort> sorts;
}