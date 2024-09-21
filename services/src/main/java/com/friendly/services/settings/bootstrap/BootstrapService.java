package com.friendly.services.settings.bootstrap;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.FTPageDetails;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.request.IntIdsRequest;
import com.friendly.commons.models.settings.bootstrap.BootstrapLWM2M;
import com.friendly.commons.models.settings.bootstrap.BootstrapLog;
import com.friendly.commons.models.settings.bootstrap.BootstrapLogDetail;
import com.friendly.commons.models.settings.iot.*;
import com.friendly.commons.models.settings.iot.response.BootstrapLogDetailsResponse;
import com.friendly.commons.models.settings.resource.AbstractResource;
import com.friendly.commons.models.settings.resource.ResourceDetails;
import com.friendly.commons.models.settings.resource.ResourceDetailsItem;
import com.friendly.commons.models.user.Session;
import com.friendly.commons.models.user.UserResponse;
import com.friendly.services.settings.bootstrap.orm.acs.repository.BootstrapLogDetailLwm2mRepository;
import com.friendly.services.settings.bootstrap.orm.acs.repository.BootstrapLogLwm2mRepository;
import com.friendly.services.settings.bootstrap.orm.acs.repository.BootstrapLwm2mRepository;
import com.friendly.services.settings.bootstrap.orm.acs.repository.ResourceDetailsLwm2mRepository;
import com.friendly.services.settings.bootstrap.orm.acs.repository.ResourceObjectLwm2mRepository;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.settings.bootstrap.orm.acs.model.BootstrapLogLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.BootstrapLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.BootstrapResourceLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.BootstrapSecurityLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.BootstrapServerLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.ResourceDetailsLwm2mEntity;
import com.friendly.services.settings.bootstrap.mapper.IotMapper;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.PageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.PROPERTY_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class BootstrapService {
    @NonNull
    private final ResourceObjectLwm2mRepository resourceObjectLwm2mRepository;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final IotMapper iotMapper;

    @NonNull
    private final UserService userService;

    @NonNull
    private final ResourceService resourceService;

    @NonNull
    private final BootstrapLwm2mRepository bootstrapRepository;

    @NonNull
    private final BootstrapLogLwm2mRepository bootstrapLogRepository;

    @NonNull
    ResourceDetailsLwm2mRepository resourceDetailsLwm2mRepository;

    @NonNull
    private final BootstrapLogDetailLwm2mRepository logDetailRepository;


    public FTPage<BootstrapLWM2M> getBootstrapConfigPage(final String token,
                                                         final BootstrapConfigBody body) {
        final Session session = jwtService.getSession(token);
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(),
                body.getPageSize(), body.getSorts(), "id");
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());
        final List<BootstrapLWM2M> bootstraps;
        final FTPageDetails pageDetails;
        // LWM2M:
        final List<Page<BootstrapLwm2mEntity>> bootstrapEntities;
        if (body.getSearchParam() != null) {

            bootstrapEntities = pageable.stream()
                    .map(p -> bootstrapRepository.findAll(getBootstrapSpecification(body.getSearchParam()), p))
                    .collect(Collectors.toList());
        } else {
            bootstrapEntities = pageable.stream()
                    .map(bootstrapRepository::findAll)
                    .collect(Collectors.toList());
        }
        bootstraps = bootstrapEntities.stream()
                .map(Page::getContent)
                .flatMap(c -> c.stream()
                        .map(e -> iotMapper.entityToSimpleBootstrapLWM2M(
                                e, session.getClientType(),
                                session.getZoneId(),
                                user.getDateFormat(),
                                user.getTimeFormat())))
                .collect(Collectors.toList());
        pageDetails = PageUtils.buildPageDetails(bootstrapEntities);
        final FTPage<BootstrapLWM2M> page = new FTPage<>();
        return page.toBuilder()
                .items(bootstraps)
                .pageDetails(pageDetails)
                .build();
    }

    public static Specification<BootstrapLwm2mEntity> getBootstrapSpecification(String searchParam) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(root.get("name").as(String.class), "%" + searchParam + "%"),
                        criteriaBuilder.like(root.get("mask").as(String.class), "%" + searchParam + "%")
                );
    }

    public BootstrapLWM2M getBootstrapConfig(final String token, final BootstrapConfigDetailsBody body) {
        jwtService.getSession(token);
        final Integer id = body.getId();
        List<AbstractResource> items = resourceService.getIotResources(id);


        // LWM2M:
        BootstrapLWM2M bootstrapLWM2M = bootstrapRepository.findById(id)
                .map(iotMapper::entityToBootstrapLWM2M)
                .orElseThrow(() -> new FriendlyEntityNotFoundException(PROPERTY_NOT_FOUND,
                        id));
        bootstrapLWM2M.setResources(items);
        return bootstrapLWM2M;

    }

    private void handleMainResource(AbstractResource resource, BootstrapResourceLwm2mEntity tempEntity) {
        if(resource.getItems() == null || resource.getItems().isEmpty()) {
            resource.getParameters().forEach(item -> {
                BootstrapResourceLwm2mEntity tempEntityPar = BootstrapResourceLwm2mEntity.builder()
                        .bootstrap(tempEntity.getBootstrap())
                        .instanceId(tempEntity.getInstanceId())
                        .objectId(tempEntity.getObjectId())
                        .created(tempEntity.getCreated())
                        .creator(tempEntity.getCreator())
                        .updated(tempEntity.getUpdated())
                        .updator(tempEntity.getUpdator())
                        .instanceId(0)
                        .build();
                saveResourceParameter(item, tempEntityPar);
            });
        }
        else {
            resource.getItems().forEach(item -> {
                BootstrapResourceLwm2mEntity tempEntityPar = BootstrapResourceLwm2mEntity.builder()
                        .bootstrap(tempEntity.getBootstrap())
                        .objectId(tempEntity.getObjectId())
                        .created(tempEntity.getCreated())
                        .creator(tempEntity.getCreator())
                        .updated(tempEntity.getUpdated())
                        .updator(tempEntity.getUpdator())
                        .instanceId(item.getInstanceId())
                        .build();
                saveItemsAndParams(item, tempEntityPar);
            });
        }
    }

    @Transactional
    public Integer addBootstrapConfig(final String token, final BootstrapLWM2M config) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        String zoneId = session.getZoneId();
        final String userName = userService.getUser(session.getUserId(), zoneId).getName();

        // LWM2M:
        Optional<BootstrapLwm2mEntity> entity = config.getId() == null ?
                Optional.empty() : bootstrapRepository.findById(config.getId());
        Instant created;
        String creator;
        if(entity.isPresent()) {
            created = entity.get().getCreated();
            creator = entity.get().getCreator();
            bootstrapRepository.deleteAllServersById(config.getId());
            bootstrapRepository.deleteAllSecuritiesById(config.getId());
        } else {
            created = Instant.now();
            creator = clientType.name().toUpperCase() + "/" + userName;
        }
        boolean isNew = config.getId() == null;

        BootstrapLwm2mEntity s = iotMapper.bootstrapLWM2MtoEntity(config, clientType, isNew,
                clientType.name().toUpperCase() + "/" + userName,
                created, creator, zoneId);
        if(config.getId() != null) {
            bootstrapRepository.deleteAllResourcesByBsId(config.getId());
        }

        List<BootstrapSecurityLwm2mEntity> securities = s.getSecurities();
        List<BootstrapServerLwm2mEntity> servers = s.getServers();

        Integer idToReturn = bootstrapRepository.save(s).getId();
        if(isNew) {
            BootstrapLwm2mEntity bootstrapLwm2mEntity = bootstrapRepository.findById(idToReturn).get();
            bootstrapLwm2mEntity.setSecurities(securities);
            bootstrapLwm2mEntity.setServers(servers);
            bootstrapRepository.save(s);
        }


        List<AbstractResource> resources = config.getResources();
        resources.forEach(resource -> {
            BootstrapResourceLwm2mEntity tempEntity = BootstrapResourceLwm2mEntity.builder()
                    .updator(clientType.name().toUpperCase() + "/" + userName)
                    .creator(creator)
                    .updated(Instant.now())
                    .created(created)
                    .bootstrap(config.getId() == null ? s : bootstrapRepository.findById(config.getId()).orElse(null))
                    .objectId(resource.getObjectId())
                    .build();
            handleMainResource(resource, tempEntity);
        });

        return idToReturn;
    }

    private void saveItemsAndParams(ResourceDetailsItem res, BootstrapResourceLwm2mEntity tempEntity) {
        res.getParameters().forEach(item -> {
            BootstrapResourceLwm2mEntity tempEntityPar = BootstrapResourceLwm2mEntity.builder()
                    .bootstrap(tempEntity.getBootstrap())
                    .instanceId(tempEntity.getInstanceId())
                    .objectId(tempEntity.getObjectId())
                    .created(tempEntity.getCreated())
                    .creator(tempEntity.getCreator())
                    .updated(tempEntity.getUpdated())
                    .updator(tempEntity.getUpdator())
                    .build();
            saveResourceParameter(item, tempEntityPar);
        });

        if(res.getItems() != null) {
            res.getItems().forEach(item -> saveItemsAndParams(item, tempEntity));
        }
    }



    private void saveResourceParameter(ResourceDetails resourceDetails, BootstrapResourceLwm2mEntity tempEntity) {
        BootstrapResourceLwm2mEntity entity = BootstrapResourceLwm2mEntity.builder()
                .bootstrap(tempEntity.getBootstrap())
                .instanceId(tempEntity.getInstanceId())
                .objectId(tempEntity.getObjectId())
                .created(tempEntity.getCreated())
                .creator(tempEntity.getCreator())
                .updated(tempEntity.getUpdated())
                .updator(tempEntity.getUpdator())
                .resourceInstanceId(getResourceInstanceId(resourceDetails.getName()))
                .resourceId(getResourceId(resourceDetails.getName(), tempEntity.getObjectId()))
                .value(resourceDetails.getValue())
                .build();

        resourceObjectLwm2mRepository.save(entity);
    }

    private Integer getResourceInstanceId(String name) {
        String[] parts = name.split("\\.");
        if (parts.length > 1) {
            String lastPart = parts[parts.length - 1];
            if (!lastPart.isEmpty()) {
                return Integer.parseInt(lastPart);
            }
        }
        return null;
    }

    private Integer getResourceId(String name, Integer objectId) {
        String pattern = "\\.[0-9]+";
        name = name.replaceAll(pattern, "");
        List<ResourceDetailsLwm2mEntity> list = resourceDetailsLwm2mRepository.findByName(name);
        return list.get(0).getResourceId();
    }

    @Transactional
    public boolean deleteBootstrapConfig(final String token, final IntIdsRequest request) {
        jwtService.getSession(token);
        final List<BootstrapLwm2mEntity> bootstraps = bootstrapRepository.findAllById(request.getIds());
        if (bootstraps.isEmpty()) {
            return false;
        } else {
            bootstrapRepository.deleteInBatch(bootstraps);
        }
        return true;
    }

    public FTPage<BootstrapLog> getBootstrapLogPage(final String token,
                                                    final BootstrapLogBody body) {
        final Session session = jwtService.getSession(token);
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(),
                body.getPageSize(), body.getSorts(), "id");
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());
        final FTPage<BootstrapLog> page = new FTPage<>();
        final List<BootstrapLog> logs;
        final FTPageDetails pageDetails;
        // LWM2M:
        final List<Page<BootstrapLogLwm2mEntity>> logEntities;
        if (body.getSearchParam() != null) {
            logEntities = pageable.stream()
                    .map(p -> bootstrapLogRepository.findAll(getBootstrapLogSpecification(body.getSearchParam(), body.isSearchExact()), p))
                    .collect(Collectors.toList());
        } else {
            logEntities = pageable.stream()
                    .map(bootstrapLogRepository::findAll)
                    .collect(Collectors.toList());
        }

        logs = logEntities.stream()
                .map(Page::getContent)
                .flatMap(c -> c.stream()
                        .map(e -> iotMapper.entityToBootstrapLog(e, session.getClientType(),
                                session.getZoneId(),
                                user.getDateFormat(),
                                user.getTimeFormat())))
                .collect(Collectors.toList());
        pageDetails = PageUtils.buildPageDetails(logEntities);
        return page.toBuilder()
                .items(logs)
                .pageDetails(pageDetails)
                .build();
    }

    public static Specification<BootstrapLogLwm2mEntity> getBootstrapLogSpecification(String searchParam, boolean exactMatch) {
        return (root, query, criteriaBuilder) ->
                exactMatch ? criteriaBuilder.equal(root.get("endpointName").as(String.class), searchParam) :
                        criteriaBuilder.like(root.get("endpointName").as(String.class), "%" + searchParam + "%");
    }

    public BootstrapLogDetailsResponse getBootstrapLogDetails(final String token, final BootstrapLogDetailsBody body) {
        final Session session = jwtService.getSession(token);
        final UserResponse user = userService.getUser(session.getUserId(), session.getZoneId());

        // LWM2M:
        List<BootstrapLogDetail> details = logDetailRepository.findAllByLogId(body.getId())
                .stream()
                .map(d -> iotMapper.entityToBootstrapLogDetail(d, session.getClientType(),
                        session.getZoneId(),
                        user.getDateFormat(),
                        user.getTimeFormat()))
                .collect(Collectors.toList());
        return new BootstrapLogDetailsResponse(details);
    }

    @Transactional
    public boolean deleteBootstrapLog(final String token, final DeleteBootstrapConfigBody body) {
        jwtService.getSession(token);

        final List<BootstrapLogLwm2mEntity> bootstraps = bootstrapLogRepository.findAllById(body.getIds());
        if (bootstraps.isEmpty()) {
            return false;
        } else {
            bootstrapLogRepository.deleteInBatch(bootstraps);
            logDetailRepository.deleteAllByLogIdIn(bootstraps.stream()
                    .map(BootstrapLogLwm2mEntity::getId)
                    .collect(Collectors.toList()));
        }
        return true;
    }

    @Transactional
    public boolean deleteAllBootstrapLog(final String token, final DeleteAllBootstrapLogBody body) {
        jwtService.getSession(token);

        if (body.getSearchParam() != null && !body.getSearchParam().isEmpty()) {
            final List<BootstrapLogLwm2mEntity> logEntities =
                    bootstrapLogRepository.findAll(getBootstrapLogSpecification(body.getSearchParam(), body.isSearchExact()));
            bootstrapLogRepository.deleteInBatch(logEntities);
            logDetailRepository.deleteAllByLogIdIn(logEntities.stream()
                    .map(BootstrapLogLwm2mEntity::getId)
                    .collect(Collectors.toList()));
        } else {
            bootstrapLogRepository.deleteAll();
            logDetailRepository.deleteAll();
        }
        return true;
    }
}
