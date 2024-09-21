package com.friendly.commons.models.device.diagnostics;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum DiagnosticType {
    TRACE_ROUTE_DIAGNOSTIC("TraceRouteDiagnostic", "Trace diagnostics", "traceroute"),
    NS_LOOKUP_DIAGNOSTIC("NSLookupDiagnostic", "NSLookup diagnostics", "nslookupdiagnostic"),
    DSL_DIAGNOSTIC( "DSLDiagnostic", "DSL diagnostics", "wandsldiagnostics"),
    IP_PING_DIAGNOSTIC("IPPingDiagnostic", "IPPing diagnostics", "ippingdiagnostic"),
    DOWNLOAD_DIAGNOSTIC("DownloadDiagnostic", "Download diagnostics", "download"),
    UPLOAD_DIAGNOSTIC("UploadDiagnostic", "Upload diagnostics", "upload"),
    LOOPBACK_DIAGNOSTIC("LoopbackDiagnostic", "Loopback diagnostics", "wanatmf5loopbackdiagnostics"),
    UDP_ECHO_DIAGNOSTIC("UDPEchoDiagnostic", "UDP echo diagnostics", "udpecho"),
    NEIGHBORING_WI_FI_DIAGNOSTIC("NeighboringWiFiDiagnostic", "Wi-Fi neighboring diagnostics", "neighboringwifidiagnostic");

    private static final DiagnosticType[] ALL_DIAGNOSTICS = DiagnosticType.values();

    @JsonValue
    private final String name;
    private final String description;
    private final String partialName;

    public static DiagnosticType fromName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("DiagnosticType cannot be null");
        }

        Optional<DiagnosticType> diagnosticType = Arrays.stream(ALL_DIAGNOSTICS)
                .filter(type -> name.equalsIgnoreCase(type.getName()))
                .findFirst();

        return diagnosticType.orElseThrow(() -> new IllegalArgumentException("No matching enum constant for the name: " + name));
    }


    public static DiagnosticType fromPartialName(String partialName) {
        if (partialName == null) {
            throw new IllegalArgumentException("DiagnosticsType cannot be null");
        }

        Optional<DiagnosticType> diagnosticType = Arrays.stream(ALL_DIAGNOSTICS)
                .filter(type -> partialName.toLowerCase().contains(type.getPartialName()))
                .findFirst();

        return diagnosticType.orElseThrow(() -> new IllegalArgumentException("No matching enum constant for the partialName: " + partialName));
    }

}

