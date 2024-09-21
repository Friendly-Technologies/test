package com.friendly.commons.models.device;

import com.friendly.commons.models.FieldSort;
import com.friendly.commons.models.view.ViewCondition;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model that represents API version of Device Columns
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceExtParamsBody  implements Serializable  {

    protected ProtocolType protocolType;
    protected String searchColumn;
    protected String searchParam;
    protected Boolean searchExact;

    protected Long exceptDeviceId;

    protected List<FieldSort> sorts;
    protected DeviceDisplayType displayType;
    protected Long viewId;
    protected List<Integer> pageNumbers;
    protected Integer pageSize;
    private List<ViewCondition> conditions;

}
