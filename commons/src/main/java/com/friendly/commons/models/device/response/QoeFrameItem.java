package com.friendly.commons.models.device.response;

import com.friendly.commons.models.device.ProtocolType;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class QoeFrameItem implements Serializable {
    private Long id;
    private String name;
    private String parameterName;
    private ProtocolType protocol;
    private Integer height;
    private SortType sort;
    private SortParameter setDefault;
    private ModeType mode;
    private Integer days;

}
