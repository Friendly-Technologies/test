package com.friendly.commons.models.device.diagnostics;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.friendly.commons.models.device.diagnostics.serializer.BooleanAsStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that represents API version of Device Diagnostic Detail
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosticDetail implements Serializable {

    private String parameter;
    @JsonSerialize(using = BooleanAsStringSerializer.class)
    private String value;
    private String key;

}
