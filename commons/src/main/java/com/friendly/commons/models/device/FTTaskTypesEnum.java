package com.friendly.commons.models.device;

public enum FTTaskTypesEnum {

    GetRPCMethods(0),
    DownloadProfile(1),
    Reboot(2),

    SetParameterValues(4),
    SetParameterValuesProfile(4),

    GetParameterNames(5),
    GetParameterNamesRetrieve(5),
    GetParameterNamesOnly(5),
    ObserveParameter(5),

    FactoryReset(9),
    ScheduleInform(10), //That functionality is not supported yet
    SetParameterValuesProvision(11),
    SetParameterValuesObjectProvision(12),
    GetParameterValuesProfileCollector(13),
    GetParameterValuesForCpeCollector(14),
    RequestDiagnostic(15),
    DiagnosticComplete(16),
    GetOptions(19),
    AddObjectProfile(21),
    SetParameterValuesObjectProfile(22), //That functionality is not supported yet;
    SetParameterNotificationProfile(23),
    SetParameterAccessProfile(24),
    AddObjectProvision(25),
    GetParameterValueList(27),
    GetParameterAttributesList(28),
    Download(29),
    DeleteObject(33),
    Upload(36),
    CustomRPC(37),
    ResetHttpSession(38),
    SetParameterAttributesProvision(39),
    ChangeDUState(41),
    SetPeriodic_QoE(42),
    SetNotification_QoE(43),
    WiFiChannelRescanQoE(44),
    UploadUpdateGroup(50),
    CustomRPCUpdateGroup(51),
    SetParameterValuesUpdateGroup(52),
    SetParameterAttributesUpdateGroup(53),
    DownloadUpdateGroup(54),
    GetParamUpdateGroup(55),
    ReProvisionUpdateGroup(56),
    BackupCpeConfiguration(57),
    RestoreCpeConfiguration(58),
    ChangeDUStateUpdateGroup(59),
    XMPPSettings(60),
    SetBootstrapParameterValues(61),

    ExecuteMethod(62),
    DiscoverParameterValueList(63),

    BackupProfile(64),
    SendCpeParameter(65),

    DiagnosticUpdateGroup(66),

    CallApiAction(100),
    AddToProvisionAction(101),
    UdpPingAction(102),

    UNKNOWN(-1);

    private int code;

    FTTaskTypesEnum(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static FTTaskTypesEnum getByName(String name){
        for (FTTaskTypesEnum ftTaskType : values()) {
            if(ftTaskType.name().equals(name)){
                return ftTaskType;
            }
        }

        throw new RuntimeException("Unknown task type name: " + name);
    }

    public static FTTaskTypesEnum getByCode(int code){
        for (FTTaskTypesEnum ftTaskType : values()) {
            if(ftTaskType.getCode() == code){
                return ftTaskType;
            }
        }
        throw new RuntimeException("Unknown task type code: " + code);
    }
}
