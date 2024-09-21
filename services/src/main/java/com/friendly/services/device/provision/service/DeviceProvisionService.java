package com.friendly.services.device.provision.service;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.DeleteDeviceProvisionsBody;
import com.friendly.commons.models.device.DeviceProvisionBody;
import com.friendly.commons.models.tabs.DeviceTab;
import com.friendly.commons.models.device.EditDeviceProvisionBody;
import com.friendly.commons.models.device.ProtocolType;
import com.friendly.commons.models.device.ProvisionParam;
import com.friendly.commons.models.device.file.DeliveryMethodType;
import com.friendly.commons.models.device.file.DeliveryProtocolType;
import com.friendly.commons.models.device.provision.AbstractProvision;
import com.friendly.commons.models.device.provision.AbstractProvisionRequest;
import com.friendly.commons.models.device.provision.DownloadProvisionDetails;
import com.friendly.commons.models.device.provision.DownloadProvisionDetailsBody;
import com.friendly.commons.models.device.provision.ProvisionDownloadRequest;
import com.friendly.commons.models.device.provision.ProvisionObject;
import com.friendly.commons.models.device.provision.ProvisionObjectRequest;
import com.friendly.commons.models.device.provision.ProvisionParameterRequest;
import com.friendly.commons.models.device.provision.ProvisionRpcRequest;
import com.friendly.commons.models.settings.ServerDetails;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.device.info.mapper.DeviceMapper;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.device.provision.orm.acs.model.DeviceProvisionEntity;
import com.friendly.services.device.provision.orm.acs.model.DeviceProvisionFileEntity;
import com.friendly.services.device.provision.orm.acs.model.DeviceProvisionObjectEntity;
import com.friendly.services.device.provision.orm.acs.model.DeviceRpcEntity;
import com.friendly.services.device.parameterstree.orm.acs.model.projections.CpeParameterNameValueProjection;
import com.friendly.services.device.provision.orm.acs.repository.DeviceProvisionFileRepository;
import com.friendly.services.device.provision.orm.acs.repository.DeviceProvisionObjectRepository;
import com.friendly.services.device.provision.orm.acs.repository.DeviceProvisionRepository;
import com.friendly.services.device.provision.orm.acs.repository.DeviceRpcRepository;
import com.friendly.services.settings.fileserver.FileServerService;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.friendly.services.infrastructure.utils.PageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.friendly.commons.models.device.file.DeliveryMethodType.Pull;
import static com.friendly.commons.models.device.file.DeliveryMethodType.Push;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.CoAP;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.CoAPS;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.CoAPoverTCP;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.CoAPoverTLS;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.HTTP;
import static com.friendly.commons.models.device.file.DeliveryProtocolType.HTTPS;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.OPERATION_NOT_SUPPORTED;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.PROVISION_NOT_FOUND;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceProvisionService {

    @NonNull
    private final DeviceMapper deviceMapper;

    @NonNull
    private final UserService userService;

    @NonNull
    private final DeviceProvisionRepository provisionRepository;

    @NonNull
    private final DeviceRpcRepository rpcRepository;

    @NonNull
    private final DeviceProvisionObjectRepository objectRepository;

    @NonNull
    private final DeviceProvisionFileRepository fileRepository;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final FileServerService fileServerService;

    public List<DeviceTab> getProvisionTabs(ProtocolType protocolType) {
        final List<DeviceTab> provisionTabs = new ArrayList<>();

        provisionTabs.add(DeviceTab.builder()
                .name("Parameters")
                .path("parameters")
                .build());
        if (!Arrays.asList(ProtocolType.MQTT, ProtocolType.LWM2M, ProtocolType.USP).contains(protocolType)) {
            provisionTabs.add(DeviceTab.builder()
                    .name("RPC")
                    .path("rpc")
                    .build());
        }
        provisionTabs.add(DeviceTab.builder()
                .name("Objects")
                .path("objects")
                .build());
        provisionTabs.add(DeviceTab.builder()
                .name("Download")
                .path("download")
                .build());

        return provisionTabs;
    }

    public FTPage<AbstractProvision> getDeviceProvision(final String token, final DeviceProvisionBody body) {
        final Session session = jwtService.getSession(token);
        String zoneId = session.getZoneId();
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), zoneId);
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(), body.getSorts(),
                "id");
        final Long deviceId = body.getDeviceId();

        final FTPage<AbstractProvision> result = new FTPage<>();
        final List<AbstractProvision> provisions;
        switch (body.getTabPath()) {
            case "parameters":
                final List<Page<DeviceProvisionEntity>> parametersPage =
                        pageable.stream()
                                .map(p -> provisionRepository.findAllProvisionsByCpeId(deviceId, p))
                                .collect(Collectors.toList());
                provisions = parametersPage.stream()
                        .map(Page::getContent)
                        .flatMap(p -> deviceMapper.provisionEntitiesToProvisionParameters(p, session.getClientType(),
                                        zoneId, user.getDateFormat(), user.getTimeFormat())
                                .stream())
                        .collect(Collectors.toList());

                return result.toBuilder()
                        .items(provisions)
                        .pageDetails(PageUtils.buildPageDetails(parametersPage))
                        .build();
            case "rpc":
                final List<Page<DeviceRpcEntity>> rpcPage =
                        pageable.stream()
                                .map(p -> rpcRepository.findAllByCpeIdAndReprovision(deviceId, 1, p))
                                .collect(Collectors.toList());
                provisions = rpcPage.stream()
                        .map(Page::getContent)
                        .flatMap(p -> deviceMapper.provisionEntitiesToProvisionRpcs(p,
                                        session.getClientType(),
                                        user.getDateFormat(),
                                        user.getTimeFormat(),
                                        zoneId)
                                .stream())
                        .collect(Collectors.toList());

                return result.toBuilder()
                        .items(provisions)
                        .pageDetails(PageUtils.buildPageDetails(rpcPage))
                        .build();
            case "objects":
                final List<Page<DeviceProvisionObjectEntity>> objectPage =
                        pageable.stream()
                                .map(p -> objectRepository.findAllByCpeId(deviceId, p))
                                .collect(Collectors.toList());
                provisions = objectPage.stream()
                        .map(Page::getContent)
                        .flatMap(p -> deviceMapper.provisionEntitiesToProvisionObjects(p,
                                        session.getClientType(),
                                        user.getDateFormat(),
                                        user.getTimeFormat(),
                                        zoneId)
                                .stream())
                        .collect(Collectors.toList());


                return result.toBuilder()
                        .items(provisions.stream()
                                .map(this::setProvisionParams)
                                .collect(Collectors.toList()))
                        .pageDetails(PageUtils.buildPageDetails(objectPage))
                        .build();
            case "download":
                final ServerDetails serverDetails = fileServerService.getServerDetails(session.getClientType())
                        .stream()
                        .filter(s -> s.getKey().equals("DownloadHttp"))
                        .findAny()
                        .orElse(null);
                final List<Page<DeviceProvisionFileEntity>> filePage =
                        pageable.stream()
                                .map(p -> fileRepository.findAllByCpeIdAndReprovision(deviceId, 1, p))
                                .collect(Collectors.toList());
                provisions = filePage.stream()
                        .map(Page::getContent)
                        .flatMap(p -> deviceMapper.provisionEntitiesToProvisionDownloads(p,
                                        session.getClientType(),
                                        user.getDateFormat(),
                                        user.getTimeFormat(),
                                        serverDetails.getAddress(),
                                        zoneId)
                                .stream())
                        .collect(Collectors.toList());

                return result.toBuilder()
                        .items(provisions)
                        .pageDetails(PageUtils.buildPageDetails(filePage))
                        .build();
        }
        return result;
    }

    private ProvisionObject setProvisionParams(AbstractProvision provision) {
        List<CpeParameterNameValueProjection> params = objectRepository.findParamsByProvisionId(provision.getId());
        ProvisionObject provisionObject = (ProvisionObject) provision;
        provisionObject.setParameters(params.stream()
                .map(obj -> new ProvisionParam(obj.getName(), obj.getValue()))
                .collect(Collectors.toList()));
        return provisionObject;
    }


    @Transactional
    public void updateDeviceProvisions(final String token, EditDeviceProvisionBody body) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        final UserResponse user = userService.getUserByIdWithoutDomain(session.getUserId(), session.getZoneId());
        final String updater = clientType.name().toUpperCase() + "/" + user.getUsername();
        final Instant updated = DateTimeUtils.clientToServer(Instant.now(), clientType, session.getZoneId());

        body.getProvisionRequest().forEach(p -> updateDeviceProvision(p, body.getDeviceId(), updater, updated));
    }

    @Transactional
    public void updateDeviceProvision(final AbstractProvisionRequest provision, final Long deviceId,
                                      final String updater, final Instant updated) {
        final Long provisionId = provision.getId();

        switch (provision.getType()) {
            case PARAMETERS:
                final ProvisionParameterRequest parameterRequest = (ProvisionParameterRequest) provision;
                final DeviceProvisionEntity oldProvision =
                        provisionRepository.findByIdAndCpeId(provisionId, deviceId)
                                .orElseThrow(() -> new FriendlyEntityNotFoundException(PROVISION_NOT_FOUND,
                                        provisionId));
                final DeviceProvisionEntity newProvision =
                        oldProvision.toBuilder()
                                .updater(updater)
                                .updated(updated)
                                .value(parameterRequest.getValue())
                                .priority(parameterRequest.getPriority())
                                .build();
                provisionRepository.saveAndFlush(newProvision);
                break;
            case RPC:
                final ProvisionRpcRequest rpcRequest = (ProvisionRpcRequest) provision;
                final DeviceRpcEntity oldRpc =
                        rpcRepository.findByIdAndCpeId(provisionId, deviceId)
                                .orElseThrow(() -> new FriendlyEntityNotFoundException(PROVISION_NOT_FOUND,
                                        provisionId));
                final DeviceRpcEntity newRpc =
                        oldRpc.toBuilder()
                                .updater(updater)
                                .updated(updated)
                                .request(rpcRequest.getRequest())
                                .priority(rpcRequest.getPriority())
                                .build();
                rpcRepository.saveAndFlush(newRpc);
                break;
            case OBJECTS:
                final ProvisionObjectRequest objectRequest = (ProvisionObjectRequest) provision;
                final DeviceProvisionObjectEntity oldObject =
                        objectRepository.findByIdAndCpeId(provisionId, deviceId)
                                .orElseThrow(() -> new FriendlyEntityNotFoundException(PROVISION_NOT_FOUND,
                                        provisionId));
                if(objectRequest.getParameters() != null) {
                    objectRepository.deleteParamsForProvision(provisionId);
                   objectRequest.getParameters().forEach(param ->
                           objectRepository.saveProvisionParam(provisionId, updater, updated,
                           param.getName(), param.getValue()));
                }
                final DeviceProvisionObjectEntity newObject =
                        oldObject.toBuilder()
                                .updated(updated)
                                .priority(objectRequest.getPriority())
                                .build();
                objectRepository.saveAndFlush(newObject);
                break;
            case DOWNLOAD:
                final ProvisionDownloadRequest downloadRequest = (ProvisionDownloadRequest) provision;
                final DeviceProvisionFileEntity oldDownload =
                        fileRepository.findByIdAndCpeId(provisionId, deviceId)
                                .orElseThrow(() -> new FriendlyEntityNotFoundException(PROVISION_NOT_FOUND,
                                        provisionId));
                final DeviceProvisionFileEntity newDownload =
                        oldDownload.toBuilder()
                                .updater(updater)
                                .updated(updated)
                                .priority(downloadRequest.getPriority())
                                .delay(downloadRequest.getDelay())
                                .fileTypeId(downloadRequest.getFileTypeId())
                                .description(downloadRequest.getDescription())
                                .fileSize(downloadRequest.getFileSize())
                                .url(downloadRequest.getLink() == null && downloadRequest.getFileName() != null
                                        && !downloadRequest.getUrl().endsWith(downloadRequest.getFileName()) ?
                                        downloadRequest.getUrl().endsWith("/") ? downloadRequest.getUrl() + downloadRequest.getFileName()
                                                : downloadRequest.getUrl() + "/" + downloadRequest.getFileName()
                                        : downloadRequest.getLink())
                                .username(downloadRequest.getUsername())
                                .password(downloadRequest.getPassword())
                                .targetFileName(downloadRequest.getTargetFileName())
                                .build();
                fileRepository.saveAndFlush(newDownload);
                break;
            default:
                throw new FriendlyIllegalArgumentException(OPERATION_NOT_SUPPORTED, "Update provision file");

        }
    }

    @Transactional
    public void deleteProvisions(final String token, final DeleteDeviceProvisionsBody body) {
        jwtService.getSession(token);
        final List<Long> ids = body.getProvisionsIds();
        final Long deviceId = body.getDeviceId();

        switch (body.getProvisionType()) {
            case PARAMETERS:
                ids.forEach(id -> {
                    if (provisionRepository.existsByIdAndCpeId(id, deviceId)) {
                        provisionRepository.deleteById(id);
                    }
                });
                break;
            case RPC:
                ids.forEach(id -> rpcRepository.findByIdAndCpeIdAndReprovision(id, deviceId, 1)
                        .ifPresent(deviceRpcEntity -> rpcRepository.saveAndFlush(deviceRpcEntity.toBuilder()
                                .reprovision(0)
                                .build())));
                break;
            case OBJECTS:
                ids.forEach(id -> {
                    if (objectRepository.existsByIdAndCpeId(id, deviceId)) {
                        objectRepository.deleteById(id);
                    }
                });
                break;
            case DOWNLOAD:
                ids.forEach(id -> fileRepository.findByIdAndCpeIdAndReprovision(id, deviceId, 1)
                        .ifPresent(deviceRpcEntity ->
                                fileRepository.saveAndFlush(deviceRpcEntity.toBuilder()
                                        .reprovision(0)
                                        .build())));
                break;
        }
    }

    public DownloadProvisionDetails getProvisionDetails(String token, DownloadProvisionDetailsBody body) {

        Session session = jwtService.getSession(token);
        long id = body.getId();
        final ServerDetails serverDetails = fileServerService.getServerDetails(session.getClientType())
                .stream()
                .filter(s -> s.getKey().equals("DownloadHttp"))
                .findAny()
                .orElse(null);
        DeviceProvisionFileEntity entity = fileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid file ID"));


        String link = entity.getUrl();
        String url = link == null || !link.contains("/") ? "" : link.substring(0, link.lastIndexOf("/") + 1);
        String fileName = link == null || !link.contains("/") ? "" : link.substring(link.lastIndexOf("/") + 1);

        return DownloadProvisionDetails.builder()
                .id(entity.getId())
                .priority(entity.getPriority())
                .delay(entity.getDelay())
                .description(entity.getDescription())
                .fileType(entity.getFileType())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .fileSize(entity.getFileSize())
                .deliveryMethod(getDeliveryMethodById(entity.getDeliveryMethod()))
                .deliveryProtocol(getDeliveryProtocolById(entity.getDeliveryProtocol()))
                .url(url)
                .link(link)
                .fileName(fileName)
                .isManual(serverDetails != null && serverDetails.getAddress() != null && !url.contains(serverDetails.getAddress()))
                .targetFileName(entity.getTargetFileName())
                .build();
    }

    private DeliveryProtocolType getDeliveryProtocolById(Integer deliveryProtocol) {
        if(deliveryProtocol == null) {
            return DeliveryProtocolType.NotSet;
        }
        switch (deliveryProtocol) {
            case 0:
                return CoAP;
            case 1:
                return CoAPS;
            case 2:
                return HTTP;
            case 3:
                return HTTPS;
            case 4:
                return CoAPoverTCP;
            case 5:
                return CoAPoverTLS;
            default:
                return DeliveryProtocolType.NotSet;
        }
    }


    private DeliveryMethodType getDeliveryMethodById(Integer deliveryMethod) {
        if(deliveryMethod == null) {
            return DeliveryMethodType.NotSet;
        }
        switch (deliveryMethod) {
            case 0:
                return Pull;
            case 1:
                return Push;
            default:
                return DeliveryMethodType.NotSet;
        }
    }

}
