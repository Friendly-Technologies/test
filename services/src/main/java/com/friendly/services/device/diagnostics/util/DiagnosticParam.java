package com.friendly.services.device.diagnostics.util;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DiagnosticParam {
    DIAGNOSTICS_TYPE("DiagnosticsType", "Diagnostics type"),
    DIAGNOSTICS_STATE("DiagnosticsState", "DiagnosticsState"),
    BOM_TIME("BomTime", "BOMTime"),
    EOM_TIME("EomTime", "EOMTime"),
    TEST_FILE_LENGTH("TestFileLength", "TestFileLength"),
    TOTAL_BYTES_SENT("TotalBytesSent", "TotalBytesSent"),
    ENABLE_PER_CONNECTION_RESULTS("EnablePerConnectionResults", "EnablePerConnectionResults"),
    TOTAL_BYTES_RECEIVED("TotalBytesReceived", "TotalBytesReceived"),
    INTERFACE("Interface", "Interface"),
    NUMBER_OF_CONNECTIONS("NumberOfConnections", "NumberOfConnections"),
    TIME_BASED_TEST_DURATION("TimeBasedTestDuration", "TimeBasedTestDuration"),
    TIME_BASED_TEST_MEASUREMENT_INTERVAL("TimeBasedTestMeasurementInterval", "TimeBasedTestMeasurementInterval"),
    TIME_BASED_TEST_MEASUREMENT_OFFSET("TimeBasedTestMeasurementOffset", "TimeBasedTestMeasurementOffset"),
    ROM_TIME("RomTime", "ROMTime"),
    UPLOAD_URL("UploadUrl", "UploadURL"),
    DOWNLOAD_URL("DownloadUrl", "DownloadURL"),
    SUCCESS_COUNT("SuccessCount", "SuccessCount"),
    RESULT_NUMBER_OF_ENTRIES("ResultNumberOfEntries", "ResultNumberOfEntries"),
    TIMEOUT("Timeout", "Timeout"),
    NUMBER_OF_REPETITIONS("NumberOfRepetitions", "NumberOfRepetitions"),
    HOST_NAME("HostName", "HostName"),
    DNS_SERVER("DnsServer", "DNSServer"),
    TEST_BYTES_RECEIVED("TestBytesReceived", "TestBytesReceived"),
    ACTP_SD_DS("ActpSdDs", "ACTPSDds"),
    ACTP_SD_US("ActpSdUs", "ACTPSDus"),
    HLIN_PS_DS("HlinPsDs", "HLINpsds"),
    QLN_PS_DS("QlnPsDs", "QLNpsds"),
    ACTA_TP_DS("ActaTpDs", "ACTATPds"),
    ACTA_TP_US("ActaTpUs", "ACTATPus"),
    HLIN_SC_DS("HlinScDs", "HLINSCds"),
    SNR_PS_DS("SnrPsDs", "SNRpsds"),
    BITS_PS_DS("BitsPsDs", "BITSpsds"),
    GAINS_PS_DS("GainsPsDs", "GAINSpsds"),
    DATA_BLOCK_SIZE("DataBlockSize", "DataBlockSize"),
    DSCP("Dscp", "DSCP"),
    HOST("Host", "Host"),
    MAX_HOP_COUNT("MaxHopCount", "MaxHopCount"),
    NUMBER_OF_TRIES("NumberOfTries", "NumberOfTries"),
    RESPONSE_TIME("ResponseTime", "ResponseTime"),
    ROUTE_HOPS_NUMBER_OF_ENTRIES("RouteHopsNumberOfEntries", "RouteHopsNumberOfEntries"),
    FAILURE_COUNT("FailureCount", "FailureCount"),
    AVERAGE_RESPONSE_TIME("AverageResponseTime", "AverageResponseTime"),
    MINIMUM_RESPONSE_TIME("MinimumResponseTime", "MinimumResponseTime"),
    MAXIMUM_RESPONSE_TIME("MaximumResponseTime", "MaximumResponseTime"),
    ENABLE_INDIVIDUAL_PACKET_RESULTS("EnableIndividualPacketResults", "EnableIndividualPacketResults"),
    INDIVIDUAL_PACKET_RESULT_NUMBER_OF_ENTRIES("IndividualPacketResultNumberOfEntries", "IndividualPacketResultNumberOfEntries"),
    INTER_TRANSMISSION_TIME("InterTransmissionTime", "InterTransmissionTime"),
    IP_ADDRESS_USED("IpAddressUsed", "IPAddressUsed"),
    PORT("Port", "Port"),
    PROTOCOL_VERSION("ProtocolVersion", "ProtocolVersion"),
    UDP_ECHO_DIAGNOSTICS_MAX_RESULTS("UdpEchoDiagnosticsMaxResults", "UDPEchoDiagnosticsMaxResults"),
    STATUS("Status", "Status"),
    LOOP_DIAGNOSTICS_STATE("LoopDiagnosticsState", "LoopDiagnosticsState"),
    RESULT("Result", "Result."),
    ROUTE_HOPS("RouteHops", "RouteHops."),
    INDIVIDUAL_PACKET_RESULT("IndividualPacketResult", "IndividualPacketResult."),
    NUMBER_OF_ROUTE_HOPS("NumberOfRouteHops", "NumberOfRouteHops"),
    NS_LOOK_UP_HOP_TYPE("HopType", "AnswerType"),
    NS_LOOK_UP_HOP_ANSWER("HopAnswer", "IPAddresses"),
    NS_LOOK_UP_HOP_RESPONSE_TIME("HopResponseTime", "ResponseTime"),
    NS_LOOK_UP_HOP_DOMAIN("HopDomain", "HostNameReturned");

    @JsonValue
    private final String name;
    private final String paramName;

    DiagnosticParam(String name, String paramName) {
        this.name = name;
        this.paramName = paramName;
    }

    public String getName() {
        return name;
    }

    public String getParamName() {
        return paramName;
    }
}

