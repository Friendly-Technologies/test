package com.friendly.services.settings.bootstrap.mapper;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.bootstrap.BootstrapLWM2M;
import com.friendly.commons.models.settings.bootstrap.BootstrapLog;
import com.friendly.commons.models.settings.bootstrap.BootstrapLogDetail;
import com.friendly.commons.models.settings.bootstrap.BootstrapResource;
import com.friendly.commons.models.settings.bootstrap.BootstrapSecurity;
import com.friendly.commons.models.settings.bootstrap.BootstrapServer;
import com.friendly.commons.models.settings.bootstrap.InstanceType;
import com.friendly.commons.models.settings.resource.ResourceDetails;
import com.friendly.commons.models.settings.resource.ResourceDetailsItem;
import com.friendly.commons.models.settings.resource.ResourceLWM2M;
import com.friendly.commons.models.settings.resource.ResourceType;
import com.friendly.commons.models.settings.security.SecurityUspMtp;
import com.friendly.commons.models.settings.security.SecurityUspMtpBasic;
import com.friendly.commons.models.settings.security.SecurityUspMtpPSK;
import com.friendly.commons.models.settings.security.SecurityUspMtpRaw;
import com.friendly.commons.models.settings.security.SecurityUspMtpX509;
import com.friendly.services.settings.bootstrap.orm.acs.model.BootstrapLogDetailLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.BootstrapLogLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.BootstrapLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.BootstrapResourceLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.BootstrapSecurityLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.BootstrapServerLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.ResourceDetailsLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.ResourcesLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.SecurityUspMtpEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.projections.Lwm2mResourceInstancesProjection;
import com.friendly.services.settings.bootstrap.orm.acs.model.projections.Lwm2mResourceProjection;
import com.friendly.services.settings.bootstrap.orm.acs.repository.SecurityLwm2mRepository;
import com.friendly.services.settings.bootstrap.Lwm2mInstanceEntity;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IotMapper {

    @NonNull
    private final SecurityLwm2mRepository lwm2mRepository;


    public ResourceLWM2M entityToResourceLWM2M(final ResourcesLwm2mEntity entity) {
        return ResourceLWM2M.builder()
                .id(entity.getId())
                .objectId(entity.getObjectId())
                .name(entity.getName())
                .description(entity.getDescription())
                .instanceType(entity.getInstanceType())
                .version(entity.getVersion())
                .build();
    }

    public ResourceLWM2M objectsToResourceLWM2M(final Lwm2mResourceProjection entity) {
        return ResourceLWM2M.builder()
                .id(entity.getId())
                .objectId(entity.getObjectId())
                .name(entity.getName())
                .description(entity.getDescription())
                .instanceType(entity.getInstanceType() == 1 ? ResourceType.Multiple : ResourceType.Single)
                .version(entity.getVersion())
                .build();
    }

    public ResourceLWM2M entityToResourceLWM2MWithoutDescription(final ResourcesLwm2mEntity entity) {
        return ResourceLWM2M.builder()
                .id(entity.getId())
                .objectId(entity.getObjectId())
                .name(entity.getName())
                .instanceType(entity.getInstanceType())
                .version(entity.getVersion())
                .build();
    }

    public ResourceDetails entityToResourceDetails(final ResourceDetailsLwm2mEntity entity) {
        return ResourceDetails.builder()
                .id(entity.getId())
                .objectId(entity.getObjectId())
                .name(entity.getName())
                .description(entity.getDescription())
                .instanceType(entity.getInstanceType())
                .mandatory(entity.getMandatory())
                .operations(entity.getOperations())
                .path(entity.getPath())
                .units(entity.getUnits())
                .valueRange(entity.getValueRange())
                .valueType(entity.getValueType())
                .build();
    }

    public ResourceDetailsItem entityToResourceDetailsItem(final ResourceDetailsLwm2mEntity entity) {
        return ResourceDetailsItem.builder()
                .id(entity.getId())
                .objectId(entity.getObjectId())
                .name(entity.getName())
                .description(entity.getDescription())
                .instanceType(entity.getInstanceType())
                .mandatory(entity.getMandatory())
                .operations(entity.getOperations())
                .path(entity.getPath())
                .units(entity.getUnits())
                .valueRange(entity.getValueRange())
                .valueType(entity.getValueType())
                .build();
    }

    public BootstrapLWM2M entityToSimpleBootstrapLWM2M(final BootstrapLwm2mEntity entity, final ClientType clientType,
                                                       final String zoneId, final String dateFormat,
                                                       final String timeFormat) {
        return BootstrapLWM2M.builder()
                .id(entity.getId())
                .updatedIso(DateTimeUtils.serverToUtc(entity.getUpdated(), clientType))
                .updated(DateTimeUtils.formatAcs(entity.getUpdated(), clientType, zoneId,
                        dateFormat, timeFormat))
                .updater(entity.getUpdater())
                .name(entity.getName())
                .mask(entity.getMask())
                .maskType(entity.getMaskType())
                .build();
    }

    public BootstrapLWM2M entityToBootstrapLWM2M(final BootstrapLwm2mEntity entity) {
        return BootstrapLWM2M.builder()
                .id(entity.getId())
                .name(entity.getName())
                .mask(entity.getMask())
                .maskType(entity.getMaskType())
                .securities(entity.getSecurities()
                        .stream()
                        .filter(e-> lwm2mRepository.findById(e.getSecurityId()).isPresent())
                        .map(this::entityToBootstrapSecurity)
                        .collect(Collectors.toList()))
                .servers(entity.getServers()
                        .stream()

                        .map(this::entityToBootstrapServer)
                        .collect(Collectors.toList()))
                .build();
    }

    private List<BootstrapResource> buildResources(List<BootstrapResourceLwm2mEntity> resources) {

        return resources.stream()
                .map(BootstrapResourceLwm2mEntity::getObjectId)
                .distinct()
                .map(this::getObjects)
                .map(object -> setResourcesForObject(object, resources))
                .collect(Collectors.toList());
    }

    private BootstrapResource setResourcesForObject(BootstrapResource resource,
                                       List<BootstrapResourceLwm2mEntity> entities) {
        entities.forEach(entity -> {
            if(entity.getObjectId().equals(resource.getObjectId())) {
                resource.getResources().add(entityToBootstrapResource(entity));
            }
        });
        return resource;
    }

    private BootstrapResource getObjects(Integer id) {
        return BootstrapResource.builder()
                .value(null)
                .name(lwm2mRepository.getResourceObjectNameById(id))
                .instanceType(null)
                .objectId(id)
                .resources(new ArrayList<>())
                .build();
    }

    private BootstrapResource entityToBootstrapResource(BootstrapResourceLwm2mEntity entity) {
        InstanceType instanceType = lwm2mRepository.getInstanceType(entity.getBootstrap().getId(),
                entity.getResourceId()) == 0 ? InstanceType.SINGLE : InstanceType.MULTIPLE;

        BootstrapResource resource = BootstrapResource.builder()
                .value(entity.getValue())
                .instanceType(instanceType)
                .objectId(entity.getObjectId())
                .name(lwm2mRepository.getResourceName(entity.getBootstrap().getId(),
                        entity.getResourceId()))
                .resources(new ArrayList<>())
                .build();

        if(instanceType == InstanceType.MULTIPLE) {
            resource.getResources().add(BootstrapResource.builder()
                            .value(resource.getValue())
                            .name(resource.getName() + "." + resource.getResources().size())
                            .objectId(resource.getObjectId())
                            .instanceType(InstanceType.SINGLE)
                            .resources(new ArrayList<>())
                    .build());
            resource.setValue(null);
        }
        return resource;
    }


    private BootstrapSecurity entityToBootstrapSecurity(final BootstrapSecurityLwm2mEntity securityEntity) {
        return BootstrapSecurity.builder()
                .id(securityEntity.getId())
                .instanceId(securityEntity.getInstanceId())
                .securityId(securityEntity.getSecurityId())
                .serverId(Integer.parseInt(securityEntity.getServerId()))
                .serverUri(securityEntity.getServerUri())
                .holdOffTime(securityEntity.getHoldOffTime())
                .isBootstrap(securityEntity.getIsBootstrap())
                .osInstanceId(securityEntity.getOsInstanceId())
                .securityType(securityEntity.getSecurityType())
                .build();
    }

    private BootstrapServer entityToBootstrapServer(final BootstrapServerLwm2mEntity serverEntity) {
        return BootstrapServer.builder()
                .id(serverEntity.getId())
                .instanceId(serverEntity.getInstanceId())
                .serverId(Integer.parseInt(serverEntity.getServerId()))
                .serverUri(serverEntity.getServerUri())
                .disableTimeout(serverEntity.getDisableTimeout())
                .lifeTime(serverEntity.getLifeTime())
                .minPeriod(serverEntity.getMinPeriod())
                .maxPeriod(serverEntity.getMaxPeriod())
                .binding(serverEntity.getBinding())
                .notification(serverEntity.getNotification())
                .build();
    }

    public BootstrapLwm2mEntity bootstrapLWM2MtoEntity(final BootstrapLWM2M entity, final ClientType clientType,
                                                       final boolean isNew, final String updater, Instant created,
                                                       String creator, final String zoneId) {
        final Instant updated = DateTimeUtils.clientToServer(Instant.now(), clientType, zoneId);
        final BootstrapLwm2mEntity bootstrap = BootstrapLwm2mEntity.builder()
                .id(entity.getId())
                .updater(updater)
                .updated(updated)
                .name(entity.getName())
                .mask(entity.getMask())
                .maskType(entity.getMaskType())
                .build();
        if (isNew) {
            bootstrap.setCreated(updated);
            bootstrap.setCreator(updater);
        } else {
            bootstrap.setCreated(created);
            bootstrap.setCreator(creator);
        }
        bootstrap.setSecurities(entity.getSecurities() == null ? null : entity.getSecurities()
                .stream()
                .map(s -> bootstrapSecurityToEntity(bootstrap, s))
                .collect(Collectors.toList()));
        bootstrap.setServers(entity.getServers() == null ? null : entity.getServers()
                .stream()
                .map(s -> bootstrapServerToEntity(bootstrap, s))
                .collect(Collectors.toList()));
        return bootstrap;

    }

    private BootstrapSecurityLwm2mEntity bootstrapSecurityToEntity(final BootstrapLwm2mEntity bootstrap,
                                                                   final BootstrapSecurity security) {
        return BootstrapSecurityLwm2mEntity.builder()
                .bootstrap(bootstrap)
                .id(security.getId())
                .instanceId(security.getInstanceId())
                .securityId(security.getSecurityId())
                .serverId(String.valueOf(security.getServerId()))
                .serverUri(security.getServerUri())
                .holdOffTime(security.getHoldOffTime())
                .isBootstrap(security.getIsBootstrap())
                .securityType(security.getSecurityType())
                .build();
    }

    private BootstrapServerLwm2mEntity bootstrapServerToEntity(final BootstrapLwm2mEntity bootstrap,
                                                               final BootstrapServer server) {
        return BootstrapServerLwm2mEntity.builder()
                .bootstrap(bootstrap)
                .id(server.getId())
                .instanceId(server.getInstanceId())
                .serverId(String.valueOf(server.getServerId()))
                .serverUri(server.getServerUri())
                .disableTimeout(server.getDisableTimeout())
                .lifeTime(server.getLifeTime())
                .minPeriod(server.getMinPeriod())
                .maxPeriod(server.getMaxPeriod())
                .binding(server.getBinding())
                .notification(server.getNotification())
                .build();
    }

    public BootstrapLog entityToBootstrapLog(final BootstrapLogLwm2mEntity entity, final ClientType clientType,
                                             final String zoneId, final String dateFormat,
                                             final String timeFormat) {
        return BootstrapLog.builder()
                .id(entity.getId())
                .configId(entity.getConfigId())
                .createdIso(DateTimeUtils.serverToUtc(entity.getCreated(), clientType))
                .created(DateTimeUtils.formatAcs(entity.getCreated(), clientType, zoneId,
                        dateFormat, timeFormat))
                .description(entity.getDescription())
                .endpointName(entity.getEndpointName())
                .endpointHost(entity.getEndpointHost())
                .endpointPort(entity.getEndpointPort())
                .securityType(entity.getSecurityType())
                .status(entity.getStatus())
                .configName(entity.getConfigName())
                .build();
    }

    public BootstrapLogDetail entityToBootstrapLogDetail(final BootstrapLogDetailLwm2mEntity entity,
                                                         final ClientType clientType, final String zoneId,
                                                         final String dateFormat, final String timeFormat) {
        return BootstrapLogDetail.builder()
                .id(entity.getId())
                .createdIso(DateTimeUtils.serverToUtc(entity.getCreated(), clientType))
                .created(DateTimeUtils.formatAcs(entity.getCreated(), clientType, zoneId,
                        dateFormat, timeFormat))
                .description(entity.getDescription())
                .activityType(entity.getActivityType())
                .request(entity.getRequest())
                .response(entity.getResponse())
                .sender(entity.getSender())
                .status(entity.getStatus())
                .build();
    }
    public SecurityUspMtp entityToSecurityUspMtp(final SecurityUspMtpEntity entity){
        switch (entity.getSecurityType()) {
            case NO_SEC:
                return buildSecurityUspMtpNoSec(entity);
            case PSK:
                return buildSecurityUspMtpPsk(entity);
            case X509:
            case X_509:
                return buildSecurityUspMtpX509(entity);
            case BASIC:
                return buildSecurityUspMtpBasic(entity);
            case PUBLIC_KEY:
                return buildSecurityUspMtpBasicRaw(entity);
        }
        return null;
    }

    private SecurityUspMtp buildSecurityUspMtpBasicRaw(SecurityUspMtpEntity entity) {
        return new SecurityUspMtpRaw(entity.getId(), entity.getSecurity().getId(),
                entity.getProtocolType(), entity.getSecurityType(), entity.getServerRPK(),
                entity.getClientRPK(), entity.getPrivateRPK());

    }

    private SecurityUspMtp buildSecurityUspMtpBasic(SecurityUspMtpEntity entity) {
        return new SecurityUspMtpBasic(entity.getId(), entity.getSecurity().getId(),
                entity.getProtocolType(), entity.getSecurityType(), entity.getLogin(),
                entity.getPassword());
    }

    private SecurityUspMtp buildSecurityUspMtpX509(SecurityUspMtpEntity entity) {
        return new SecurityUspMtpX509(entity.getId(), entity.getSecurity().getId(),
                entity.getProtocolType(), entity.getSecurityType(), entity.getCertificate(),
                entity.getCustomAlias());

    }

    private SecurityUspMtp buildSecurityUspMtpPsk(SecurityUspMtpEntity entity) {
        return new SecurityUspMtpPSK(entity.getId(), entity.getSecurity().getId(),
                entity.getProtocolType(), entity.getSecurityType(), entity.getPskIdentity(),
                entity.getPskSecretKey());
    }

    private SecurityUspMtp buildSecurityUspMtpNoSec(SecurityUspMtpEntity entity) {
        return SecurityUspMtp.builder()
                .id(entity.getId())
                .securityId(entity.getSecurity().getId())
                .securityType(entity.getSecurityType())
                .mtpProtocolType(entity.getProtocolType())
                .build();

    }

    public List<Lwm2mInstanceEntity> objectToInstances(List<Lwm2mResourceInstancesProjection> allInstances) {
        return allInstances.stream()
                .map(e -> new Lwm2mInstanceEntity(e.getInstanceId(), e.getResourceInstanceId(), e.getValue(), e.getName()))
                .collect(Collectors.toList());
    }
}
