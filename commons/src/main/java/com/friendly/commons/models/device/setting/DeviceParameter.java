package com.friendly.commons.models.device.setting;

import com.friendly.commons.models.tree.TreeParameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.io.StringBufferInputStream;
import java.util.List;
import java.util.Map;

/**
 * Model that represents API version of Device Columns
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeviceParameter extends TreeParameter {

    private Long id;
    private Long nameId;
    private String parentName;
    private String type;
    private Boolean canOverwrite;
    private Object value;
    private List<DeviceActionType> actions;

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Pair {
        private Integer key;
        private String value;
    }
}
