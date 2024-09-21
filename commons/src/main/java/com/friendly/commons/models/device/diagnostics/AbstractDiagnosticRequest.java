package com.friendly.commons.models.device.diagnostics;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Model that represents API version of Diagnostic Request
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = AbstractDiagnosticRequest.VALUE_TYPE_PROPERTY_NAME,
        defaultImpl = DiagnosticType.class,
        visible = true)
@JsonSubTypes({
        /* Names for sub-type mapping should be gotten from {@link ValueType} enum */
        @JsonSubTypes.Type(name = "TraceRouteDiagnostic", value = TraceRouteDiagnosticRequest.class),
        @JsonSubTypes.Type(name = "NSLookupDiagnostic", value = NSLookupDiagnosticRequest.class),
        @JsonSubTypes.Type(name = "DSLDiagnostic", value = PushDiagnosticRequest.class),
        @JsonSubTypes.Type(name = "IPPingDiagnostic", value = IPPingDiagnosticRequest.class),
        @JsonSubTypes.Type(name = "DownloadDiagnostic", value = DownloadDiagnosticRequest.class),
        @JsonSubTypes.Type(name = "UploadDiagnostic", value = UploadDiagnosticRequest.class),
        @JsonSubTypes.Type(name = "LoopbackDiagnostic", value = LoopbackDiagnosticRequest.class),
        @JsonSubTypes.Type(name = "NeighboringWiFiDiagnostic", value = PushDiagnosticRequest.class),
        @JsonSubTypes.Type(name = "UDPEchoDiagnostic", value = UdpEchoDiagnosticRequest.class)
})
public abstract class AbstractDiagnosticRequest implements Serializable {

    static final String VALUE_TYPE_PROPERTY_NAME = "diagnosticType";
    private boolean qoeTask;
    private DiagnosticType diagnosticType;
    private Boolean push;
}
