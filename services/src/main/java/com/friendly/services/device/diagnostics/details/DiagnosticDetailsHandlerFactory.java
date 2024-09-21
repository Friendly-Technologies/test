package com.friendly.services.device.diagnostics.details;

import com.friendly.commons.models.device.diagnostics.DiagnosticType;
import com.friendly.services.device.diagnostics.util.DiagnosticsDetailsUtil;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import static com.friendly.commons.models.device.diagnostics.DiagnosticType.DOWNLOAD_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.DSL_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.IP_PING_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.LOOPBACK_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.NEIGHBORING_WI_FI_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.NS_LOOKUP_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.TRACE_ROUTE_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.UDP_ECHO_DIAGNOSTIC;
import static com.friendly.commons.models.device.diagnostics.DiagnosticType.UPLOAD_DIAGNOSTIC;

public final class DiagnosticDetailsHandlerFactory {

    private DiagnosticDetailsHandlerFactory() {
        throw new IllegalStateException("Utility class");
    }

    private static final Map<DiagnosticType, Function<DiagnosticsDetailsUtil, DiagnosticDetailsHandler>>
            handlerSuppliers = new EnumMap<>(DiagnosticType.class);

    static {
        handlerSuppliers.put(UPLOAD_DIAGNOSTIC, UploadDiagnosticDetailsHandler::new);
        handlerSuppliers.put(DOWNLOAD_DIAGNOSTIC, DownloadDiagnosticDetailsHandler::new);
        handlerSuppliers.put(NS_LOOKUP_DIAGNOSTIC, NSLookupDiagnosticDetailsHandler::new);
        handlerSuppliers.put(DSL_DIAGNOSTIC, DSLDiagnosticDetailsHandler::new);
        handlerSuppliers.put(TRACE_ROUTE_DIAGNOSTIC, TraceRouteDiagnosticDetailsHandler::new);
        handlerSuppliers.put(LOOPBACK_DIAGNOSTIC, LoopbackDiagnosticDetailsHandler::new);
        handlerSuppliers.put(NEIGHBORING_WI_FI_DIAGNOSTIC, NeighboringWiFiDiagnosticDetailsHandler::new);
        handlerSuppliers.put(IP_PING_DIAGNOSTIC, IPPingDiagnosticDetailsHandler::new);
        handlerSuppliers.put(UDP_ECHO_DIAGNOSTIC, UDPEchoDiagnosticDetailsHandler::new);
    }

    public static DiagnosticDetailsHandler getHandler(DiagnosticType diagnosticType, DiagnosticsDetailsUtil diagnosticsDetailsUtil) {
        Function<DiagnosticsDetailsUtil, DiagnosticDetailsHandler> handlerFunction = handlerSuppliers.get(diagnosticType);
        if (handlerFunction == null) {
            throw new IllegalArgumentException("Unsupported diagnostic type: " + diagnosticType);
        }
        return handlerFunction.apply(diagnosticsDetailsUtil);
    }
}
