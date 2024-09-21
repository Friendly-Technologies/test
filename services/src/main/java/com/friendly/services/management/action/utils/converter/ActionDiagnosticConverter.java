package com.friendly.services.management.action.utils.converter;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.models.device.diagnostics.DiagnosticType;
import com.friendly.services.device.diagnostics.service.DeviceDiagnosticsService;
import com.friendly.services.device.diagnostics.util.DiagnosticParam;
import com.friendly.services.management.action.orm.acs.model.ActionDiagnosticEntity;
import com.friendly.services.management.action.orm.acs.model.ActionEntity;
import com.friendly.services.management.action.orm.acs.model.ActionSetValueEntity;
import com.friendly.services.device.parameterstree.service.ParameterNameService;
import com.friendly.services.management.action.dto.enums.ActionTypeEnum;
import com.friendly.services.management.action.dto.request.inheritors.DiagnosticTaskAction;
import com.friendly.services.management.action.dto.response.ActionListResponse;
import com.friendly.services.management.action.dto.response.ActionParameters;
import com.friendly.services.management.action.dto.response.diagnostictask.DiagnosticTaskActionResponse;
import com.friendly.services.management.action.dto.response.diagnostictask.DownloadDiagnosticResponse;
import com.friendly.services.management.action.dto.response.diagnostictask.IPPingDiagnosticResponse;
import com.friendly.services.management.action.dto.response.diagnostictask.LoopbackDiagnosticResponse;
import com.friendly.services.management.action.dto.response.diagnostictask.NSLookupDiagnosticResponse;
import com.friendly.services.management.action.dto.response.diagnostictask.PushDiagnosticResponse;
import com.friendly.services.management.action.dto.response.diagnostictask.TraceRouteDiagnosticResponse;
import com.friendly.services.management.action.dto.response.diagnostictask.UploadDiagnosticResponse;
import com.ftacs.CpeDiagnosticWS;
import com.ftacs.CpeParamListWS;
import com.ftacs.CpeParamWS;
import com.ftacs.DiagnosticTaskWS;
import com.ftacs.StringArrayWS;
import com.ftacs.UpdateTaskWS;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.DEVICE_PARAMETER_NOT_FOUND;


@RequiredArgsConstructor
public class ActionDiagnosticConverter implements ActionConverter<DiagnosticTaskAction, DiagnosticTaskActionResponse> {
    private final ParameterNameService parameterNameService;
    private final DeviceDiagnosticsService deviceDiagnosticsService;
    @Setter
    private Long groupId;

    @Override
    public UpdateTaskWS convertToEntity(DiagnosticTaskAction request) {
        final DiagnosticTaskWS diagnosticTaskWS = new DiagnosticTaskWS();
        final CpeParamListWS cpeParamListWS = new CpeParamListWS();
        final StringArrayWS stringArrayWS = new StringArrayWS();
        final CpeDiagnosticWS cpeDiagnosticWS = deviceDiagnosticsService.addDiagnosticForUgTask(request, groupId);

        diagnosticTaskWS.setName(cpeDiagnosticWS.getCpeDiagName());

        cpeDiagnosticWS.getCpeDiagSetParameters()
                .getCPEDiagParameter()
                .forEach(cpeDiagParameterWS -> {
                    CpeParamWS cpeParamWS = new CpeParamWS();
                    cpeParamWS.setName(cpeDiagParameterWS.getParamName());
                    cpeParamWS.setValue(cpeDiagParameterWS.getParamValue());
                    cpeParamListWS.getCPEParam().add(cpeParamWS);
                });
        diagnosticTaskWS.setParametersForSet(cpeParamListWS);

        cpeDiagnosticWS.getCpeDiagGetParameters()
                .getCPEDiagParameter()
                .forEach(cpeDiagParameterWS -> stringArrayWS.getString().add(cpeDiagParameterWS.getParamName()));
        diagnosticTaskWS.setParametersForGet(stringArrayWS);
        diagnosticTaskWS.setOrder(request.getOrder());
        diagnosticTaskWS.setQoeTask(request.getDiagnosticRequest().isQoeTask());
        return diagnosticTaskWS;
    }

    @Override
    public ActionListResponse convertToResponse(ActionEntity action) {
        ActionListResponse response = new ActionListResponse();
        response.setTaskType(ActionTypeEnum.DIAGNOSTIC_TASK);
        List<ActionParameters> parameters = new ArrayList<>();
        ActionDiagnosticEntity actionDiagnosticEntity = action.getActionDiagnosticList().get(0);
        List<ActionSetValueEntity> actionSetValueEntities = action.getActionSetValueList();
         parameters.add(ActionParameters
                 .<DiagnosticTaskActionResponse>builder()
                 .name(actionDiagnosticEntity.getName())
                 .value(getDiagnosticTaskValue(actionSetValueEntities,actionDiagnosticEntity.getName()))
                 .details(getDiagnosticTaskDetails(actionDiagnosticEntity, actionSetValueEntities))
                 .build());
        response.setOrder(action.getPriority());
        response.setParameters(parameters);
        return response;
    }

    private String getDiagnosticTaskValue(List<ActionSetValueEntity> actionSetValueList, String diagName) {
        DiagnosticType type = DiagnosticType.fromName(diagName);
        DiagnosticParam diagnosticParam;
        if(type.equals(DiagnosticType.DOWNLOAD_DIAGNOSTIC)){
            diagnosticParam = DiagnosticParam.DOWNLOAD_URL;
        }
        else if(type.equals(DiagnosticType.UPLOAD_DIAGNOSTIC)){
            diagnosticParam = DiagnosticParam.UPLOAD_URL;
        }
        else {
            diagnosticParam = DiagnosticParam.HOST;
        }
        return actionSetValueList.stream()
                .filter(e -> parameterNameService.getNameById(e.getNameId()).contains(diagnosticParam.getParamName()))
                .findFirst()
                .orElseThrow(() -> new FriendlyEntityNotFoundException(DEVICE_PARAMETER_NOT_FOUND, diagnosticParam))
                .getValue();
    }

    private DiagnosticTaskActionResponse getDiagnosticTaskDetails(ActionDiagnosticEntity a, List<ActionSetValueEntity> actionSetValueEntities) {

        switch (DiagnosticType.fromName(a.getName())){
            case DOWNLOAD_DIAGNOSTIC:
                return DownloadDiagnosticResponse.builder()
                        .diagnosticType(DiagnosticType.DOWNLOAD_DIAGNOSTIC)
                        .qoeTask(a.getQoeTask())
                        .url(findEntityByParamName(actionSetValueEntities, DiagnosticParam.DOWNLOAD_URL).getValue())
                        .build();
            case DSL_DIAGNOSTIC:
                return PushDiagnosticResponse.builder()
                        .diagnosticType(DiagnosticType.DSL_DIAGNOSTIC)
                        .qoeTask(a.getQoeTask())
                        .build();
            case UPLOAD_DIAGNOSTIC:
                return UploadDiagnosticResponse.builder()
                        .diagnosticType(DiagnosticType.UPLOAD_DIAGNOSTIC)
                        .qoeTask(a.getQoeTask())
                        .url(findEntityByParamName(actionSetValueEntities, DiagnosticParam.UPLOAD_URL).getValue())
                        .fileSize(findEntityByParamName(actionSetValueEntities, DiagnosticParam.TEST_FILE_LENGTH).getValue())
                        .build();
            case IP_PING_DIAGNOSTIC:
                return IPPingDiagnosticResponse.builder()
                        .diagnosticType(DiagnosticType.IP_PING_DIAGNOSTIC)
                        .qoeTask(a.getQoeTask())
                        .host(findEntityByParamName(actionSetValueEntities, DiagnosticParam.HOST).getValue())
                        .dataSize(Integer.valueOf(findEntityByParamName(actionSetValueEntities, DiagnosticParam.DATA_BLOCK_SIZE).getValue()))
                        .repetitions(Integer.valueOf(findEntityByParamName(actionSetValueEntities, DiagnosticParam.NUMBER_OF_REPETITIONS).getValue()))
                        .timeout(Integer.valueOf(findEntityByParamName(actionSetValueEntities, DiagnosticParam.TIMEOUT).getValue()))
                        .build();
            case LOOPBACK_DIAGNOSTIC:
                return LoopbackDiagnosticResponse.builder()
                        .diagnosticType(DiagnosticType.LOOPBACK_DIAGNOSTIC)
                        .qoeTask(a.getQoeTask())
                        .timeout(Integer.valueOf(findEntityByParamName(actionSetValueEntities, DiagnosticParam.TIMEOUT).getValue()))
                        .repetitions(Integer.valueOf(findEntityByParamName(actionSetValueEntities, DiagnosticParam.NUMBER_OF_REPETITIONS).getValue()))
                        .build();
            case UDP_ECHO_DIAGNOSTIC:

            case NS_LOOKUP_DIAGNOSTIC:
                return NSLookupDiagnosticResponse.builder()
                        .diagnosticType(DiagnosticType.NS_LOOKUP_DIAGNOSTIC)
                        .qoeTask(a.getQoeTask())
                        .dns(findEntityByParamName(actionSetValueEntities, DiagnosticParam.DNS_SERVER).getValue())
                        .host((findEntityByParamName(actionSetValueEntities, DiagnosticParam.HOST).getValue()))
                        .timeout(Integer.valueOf(findEntityByParamName(actionSetValueEntities, DiagnosticParam.TIMEOUT).getValue()))
                        .repetitions(Integer.valueOf(findEntityByParamName(actionSetValueEntities, DiagnosticParam.NUMBER_OF_REPETITIONS).getValue()))
                        .build();
            case TRACE_ROUTE_DIAGNOSTIC:
                return TraceRouteDiagnosticResponse.builder()
                        .diagnosticType(DiagnosticType.TRACE_ROUTE_DIAGNOSTIC)
                        .qoeTask(a.getQoeTask())
                        .host(findEntityByParamName(actionSetValueEntities, DiagnosticParam.HOST).getValue())
                        .maxHop(Integer.valueOf(findEntityByParamName(actionSetValueEntities, DiagnosticParam.MAX_HOP_COUNT).getValue()))
                        .dataSize(Integer.valueOf(findEntityByParamName(actionSetValueEntities, DiagnosticParam.DATA_BLOCK_SIZE).getValue()))
                        .repetitions(Integer.valueOf(findEntityByParamName(actionSetValueEntities, DiagnosticParam.NUMBER_OF_TRIES).getValue()))
                        .timeout(Integer.valueOf(findEntityByParamName(actionSetValueEntities, DiagnosticParam.TIMEOUT).getValue()))
                        .build();
            case NEIGHBORING_WI_FI_DIAGNOSTIC:
                return PushDiagnosticResponse.builder()
                        .diagnosticType(DiagnosticType.NEIGHBORING_WI_FI_DIAGNOSTIC)
                        .qoeTask(a.getQoeTask())
                        .build();
            default: return new DiagnosticTaskActionResponse();
        }
    }

    @Override
    public List<UpdateTaskWS> convertToRequest(ActionEntity action) {
        final List<UpdateTaskWS> updateTaskWSList = new ArrayList<>();

        action.getActionDiagnosticList().forEach(d -> {
            DiagnosticTaskWS diagnosticTaskWS = new DiagnosticTaskWS();
            CpeParamListWS cpeParamListWS = new CpeParamListWS();
            StringArrayWS stringArrayWS = new StringArrayWS();
            action.getActionSetValueList()
                    .forEach(e -> {
                    CpeParamWS cpeParamWS = new CpeParamWS();
                        cpeParamWS.setName(parameterNameService.getNameById(e.getNameId()));
                        cpeParamWS.setValue(e.getValue());
                        cpeParamListWS.getCPEParam().add(cpeParamWS);
                    });
            action.getActionGetParamList()
                            .forEach(e ->
                                    stringArrayWS.getString().add(parameterNameService.getNameById(e.getNameId()))
                            );
            diagnosticTaskWS.setParametersForSet(cpeParamListWS);
            diagnosticTaskWS.setParametersForGet(stringArrayWS);
            diagnosticTaskWS.setName(d.getName());
            diagnosticTaskWS.setQoeTask(d.getQoeTask());
            diagnosticTaskWS.setOrder(action.getPriority());
            updateTaskWSList.add(diagnosticTaskWS);
        });

        return updateTaskWSList;
    }

    private ActionSetValueEntity findEntityByParamName(List<ActionSetValueEntity> entities, DiagnosticParam param) {
        return entities.stream()
                .filter(e -> parameterNameService.getNameById(e.getNameId()).contains(param.getParamName()))
                .findFirst()
                .orElseThrow(() -> new FriendlyEntityNotFoundException(DEVICE_PARAMETER_NOT_FOUND, param.getParamName()));
    }
}
