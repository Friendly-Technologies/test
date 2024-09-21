package com.friendly.services.device.diagnostics.ws;

import com.friendly.commons.models.device.diagnostics.DiagnosticType;
import com.friendly.commons.models.device.diagnostics.UploadDiagnosticRequest;
import com.friendly.services.device.diagnostics.util.CpeDiagParameterUtil;
import com.friendly.services.device.parameterstree.service.ParameterService;
import com.ftacs.CpeDiagParameterListWS;
import com.ftacs.CpeDiagnosticWS;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

import static com.friendly.services.device.diagnostics.util.CpeDiagParameterUtil.createCpeDiagParameterWS;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.BOM_TIME;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.DIAGNOSTICS_STATE;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.EOM_TIME;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.INTERFACE;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.NUMBER_OF_CONNECTIONS;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.ROM_TIME;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.STATUS;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.TEST_FILE_LENGTH;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.TIME_BASED_TEST_DURATION;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.TIME_BASED_TEST_MEASUREMENT_INTERVAL;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.TIME_BASED_TEST_MEASUREMENT_OFFSET;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.TOTAL_BYTES_SENT;
import static com.friendly.services.device.diagnostics.util.DiagnosticParam.UPLOAD_URL;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UploadDiagnosticHandler implements DiagnosticToDiagnosticWsHandler {
    int protocolTypeId;
    String root;
    UploadDiagnosticRequest diagnosticRequest;
    CpeDiagParameterListWS inputParams;
    CpeDiagParameterListWS outputParams;
    CpeDiagnosticWS cpeDiagnosticWS;
    String serial;
    String defaultFileSize;
    Long deviceId;
    ParameterService parameterService;

    @Override
    public void handleDiagnosticToDiagnosticWs() {
        cpeDiagnosticWS.setCpeDiagName(DiagnosticType.UPLOAD_DIAGNOSTIC.getName());

        if (protocolTypeId == 5) {
            outputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + STATUS.getParamName()));
        } else {
            inputParams.getCPEDiagParameter()
                    .add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + DIAGNOSTICS_STATE.getParamName(), REQUESTED));
        }


        inputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + TEST_FILE_LENGTH.getParamName(),
                diagnosticRequest.getFileSize() == null ? defaultFileSize
                        : String.valueOf(diagnosticRequest.getFileSize())));
        inputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + UPLOAD_URL.getParamName(),
                diagnosticRequest.getUrl() != null || serial != null ? diagnosticRequest.getUrl() + serial : ""));
        if(diagnosticRequest.getNumberOfConnections() != null && deviceId != null &&
                parameterService.isParamExistLike(deviceId,root + NUMBER_OF_CONNECTIONS.getParamName())) {
            inputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(
                    root + NUMBER_OF_CONNECTIONS.getParamName(), diagnosticRequest.getNumberOfConnections()));
        }
        if(diagnosticRequest.getDuration() != null && deviceId != null && parameterService.isParamExistLike(deviceId,
                root + TIME_BASED_TEST_DURATION.getParamName())) {
            inputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(
                    root + TIME_BASED_TEST_DURATION.getParamName(), diagnosticRequest.getDuration()));
        }
        if(diagnosticRequest.getInterval() != null && deviceId != null && parameterService.isParamExistLike(deviceId,
                root + TIME_BASED_TEST_MEASUREMENT_INTERVAL.getParamName())) {
            inputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(
                    root + TIME_BASED_TEST_MEASUREMENT_INTERVAL.getParamName(), diagnosticRequest.getInterval()));
        }
        if(diagnosticRequest.getOffset() != null && deviceId != null && parameterService.isParamExistLike(deviceId,
                root + TIME_BASED_TEST_MEASUREMENT_OFFSET.getParamName())) {
            inputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(
                    root + TIME_BASED_TEST_MEASUREMENT_OFFSET.getParamName(), diagnosticRequest.getOffset()));
        }
        inputParams.getCPEDiagParameter().add(CpeDiagParameterUtil.createCpeDiagParameterWS(root + INTERFACE.getParamName(), ""));

        outputParams.getCPEDiagParameter()
                .addAll(Arrays.asList(CpeDiagParameterUtil.createCpeDiagParameterWS(root + BOM_TIME.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + EOM_TIME.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + ROM_TIME.getParamName()),
                        CpeDiagParameterUtil.createCpeDiagParameterWS(root + TOTAL_BYTES_SENT.getParamName())));
    }

}

