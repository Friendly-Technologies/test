package com.friendly.commons.models.device.diagnostics;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiagnosticDetails implements Serializable {

    String diagnosticsTypeKey;
    String state;
    List<DiagnosticDetail> details;
    List<Map<String, String>> table;

}
