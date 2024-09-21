package com.friendly.services.device.diagnostics.root;

import com.friendly.commons.models.device.diagnostics.DiagnosticType;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class DiagnosticRootStrategyFactory {

    private DiagnosticRootStrategyFactory() {
        throw new IllegalStateException("Utility class");
    }
    private static final Map<DiagnosticType, Supplier<DiagnosticRootStrategy>> strategySuppliers =
            new EnumMap<>(DiagnosticType.class);

    static {
        strategySuppliers.put(DiagnosticType.IP_PING_DIAGNOSTIC, IPPingDiagnosticStrategy::new);
        strategySuppliers.put(DiagnosticType.DSL_DIAGNOSTIC, DSLDiagnosticStrategy::new);
        strategySuppliers.put(DiagnosticType.TRACE_ROUTE_DIAGNOSTIC, TraceRouteDiagnosticStrategy::new);
        strategySuppliers.put(DiagnosticType.NEIGHBORING_WI_FI_DIAGNOSTIC, NeighboringWiFiDiagnosticStrategy::new);
        strategySuppliers.put(DiagnosticType.LOOPBACK_DIAGNOSTIC, LoopbackDiagnosticStrategy::new);
        strategySuppliers.put(DiagnosticType.DOWNLOAD_DIAGNOSTIC, DownloadDiagnosticStrategy::new);
        strategySuppliers.put(DiagnosticType.UPLOAD_DIAGNOSTIC, UploadDiagnosticStrategy::new);
        strategySuppliers.put(DiagnosticType.NS_LOOKUP_DIAGNOSTIC, NSLookupDiagnosticStrategy::new);
        strategySuppliers.put(DiagnosticType.UDP_ECHO_DIAGNOSTIC, UDPEchoDiagnosticStrategy::new);
    }

    public static DiagnosticRootStrategy getStrategy(DiagnosticType diagType) {
        Supplier<DiagnosticRootStrategy> strategySupplier = strategySuppliers.get(diagType);
        if (strategySupplier == null) {
            throw new IllegalArgumentException("Unsupported diagnostic type: " + diagType);
        }
        return strategySupplier.get();
    }
}
