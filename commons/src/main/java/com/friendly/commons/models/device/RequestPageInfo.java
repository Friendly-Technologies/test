package com.friendly.commons.models.device;

import com.friendly.commons.models.FieldSort;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RequestPageInfo {

    private List<Integer> pageNumbers;
    private Integer pageSize;
    private List<FieldSort> sorts;
}
