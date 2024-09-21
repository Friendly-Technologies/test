package com.friendly.services.management.action.dto.response.diagnostictask;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.friendly.services.management.action.dto.response.AbstractActionResponse;
import com.friendly.commons.models.device.diagnostics.DiagnosticType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = DiagnosticTaskActionResponse.VALUE_TYPE_PROPERTY_NAME,
        defaultImpl = DiagnosticType.class,
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "TraceRouteDiagnostic", value = TraceRouteDiagnosticResponse.class),
        @JsonSubTypes.Type(name = "NSLookupDiagnostic", value = NSLookupDiagnosticResponse.class),
        @JsonSubTypes.Type(name = "DSLDiagnostic", value = PushDiagnosticResponse.class),
        @JsonSubTypes.Type(name = "IPPingDiagnostic", value = IPPingDiagnosticResponse.class),
        @JsonSubTypes.Type(name = "DownloadDiagnostic", value = DownloadDiagnosticResponse.class),
        @JsonSubTypes.Type(name = "UploadDiagnostic", value = UploadDiagnosticResponse.class),
        @JsonSubTypes.Type(name = "LoopbackDiagnostic", value = LoopbackDiagnosticResponse.class),
        @JsonSubTypes.Type(name = "NeighboringWiFiDiagnostic", value = PushDiagnosticResponse.class),
        @JsonSubTypes.Type(name = "UDPEchoDiagnostic", value = UdpEchoDiagnosticResponse.class)
})
public class DiagnosticTaskActionResponse extends AbstractActionResponse {
    static final String VALUE_TYPE_PROPERTY_NAME = "diagnosticType";
    private DiagnosticType diagnosticType;
    private boolean qoeTask;
}
