package com.friendly.commons.models.device;

import com.friendly.commons.models.FieldSort;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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
public class BaseParams implements Serializable {

    protected String manufacturer;
    protected String model;
    protected ProtocolType protocolType;

    protected String searchColumn;
    protected String searchParam;
    protected Boolean searchExact;

    protected Long exceptDeviceId;

    protected List<FieldSort> sorts;

}
