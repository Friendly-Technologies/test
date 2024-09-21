package com.friendly.services.settings.bootstrap;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.FTPageDetails;
import com.friendly.commons.models.FieldSort;
import com.friendly.commons.models.OrderDirection;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.file.FileDownloadRequest;
import com.friendly.commons.models.device.response.TaskIdsResponse;
import com.friendly.commons.models.settings.iot.DeleteIotResourceBody;
import com.friendly.commons.models.settings.iot.IotResourceBody;
import com.friendly.commons.models.settings.iot.IotResourceDetailBody;
import com.friendly.commons.models.settings.iot.response.ResourceDetailsResponse;
import com.friendly.commons.models.settings.resource.AbstractResource;
import com.friendly.commons.models.settings.resource.ResourceDetails;
import com.friendly.commons.models.settings.resource.ResourceDetailsItem;
import com.friendly.commons.models.user.Session;
import com.friendly.services.settings.bootstrap.orm.acs.model.projections.Lwm2mResourceProjection;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.filemanagement.service.DeviceFileService;
import com.friendly.services.device.activity.service.TaskService;
import com.friendly.services.filemanagement.orm.acs.model.FileTypeEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.ResourceDetailsLwm2mEntity;
import com.friendly.services.settings.bootstrap.orm.acs.model.ResourcesLwm2mEntity;
import com.friendly.services.filemanagement.orm.acs.repository.FileTypeRepository;
import com.friendly.services.settings.bootstrap.orm.acs.repository.ResourceDetailsLwm2mRepository;
import com.friendly.services.settings.bootstrap.orm.acs.repository.ResourceLwm2mRepository;
import com.friendly.services.settings.bootstrap.mapper.IotMapper;
import com.friendly.services.uiservices.user.UserService;
import com.friendly.services.infrastructure.utils.PageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.FILE_FORMAT_NOT_SUPPORTED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final IotMapper iotMapper;

    @NonNull
    private final UserService userService;

    @NonNull
    private final DeviceFileService fileService;

    @NonNull
    private final TaskService taskService;

    @NonNull
    private final ResourceLwm2mRepository lwm2mRepository;

    @NonNull
    private final ResourceDetailsLwm2mRepository resourceDetailsLwm2mRepository;

    @NonNull
    private final ResourceDetailsLwm2mRepository detailsLwm2mRepository;

    @NonNull
    private final FileTypeRepository fileTypeRepository;

    public FTPage<AbstractResource> getIotResourcePage(final String token,
                                                       final IotResourceBody body) {
        jwtService.getSession(token);
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(),
                Collections.singletonList(
                        FieldSort.builder()
                                .field("objectId")
                                .direction(OrderDirection.ASC)
                                .build()),
                "objectId");

        final FTPage<AbstractResource> resourcePage = new FTPage<>();
        final List<AbstractResource> resources;
        final FTPageDetails pageDetails;
        // LWM2M:
        final List<Page<ResourcesLwm2mEntity>> lwm2mResources =
                pageable.stream()
                        .map(lwm2mRepository::getAll)
                        .collect(Collectors.toList());
        final List<ResourceDetailsLwm2mEntity> items =
                resourceDetailsLwm2mRepository.findAllWritableItems();
        final List<ResourceDetailsLwm2mEntity> parameters =
                resourceDetailsLwm2mRepository.findAllWritableParameters();
        final List<Lwm2mInstanceEntity> instances =
                iotMapper.objectToInstances(resourceDetailsLwm2mRepository.findAllInstances(body.getId()));

        resources = lwm2mResources.stream()
                .map(Page::getContent)
                .flatMap(entities -> entities.stream()
                        .map(iotMapper::entityToResourceLWM2M))
                .collect(Collectors.toList());
        resources.forEach(r -> setItemsAndParams(r, items, parameters, instances));
        pageDetails = PageUtils.buildPageDetails(lwm2mResources);
        return resourcePage.toBuilder()
                .items(resources)
                .pageDetails(pageDetails)
                .build();
    }

    private void setItemsAndParams(AbstractResource resource,
                                   List<ResourceDetailsLwm2mEntity> items,
                                   List<ResourceDetailsLwm2mEntity> parameters,
                                   List<Lwm2mInstanceEntity> instances) {

        List<ResourceDetailsItem> filteredItems = items.stream()
                .filter(item -> item.getObjectId().equals(resource.getId()))
                .map(iotMapper::entityToResourceDetailsItem)
                .collect(Collectors.toList());

        Map<String, Lwm2mInstanceEntity> instanceMap = instances.stream()
                .collect(Collectors.toMap(Lwm2mInstanceEntity::getName, instance -> instance));

        filteredItems.forEach(item -> {
            Lwm2mInstanceEntity instance = instanceMap.get(item.getName());
            if (instance != null) {
                if (item.getParameters() == null) {
                    item.setParameters(new ArrayList<>());
                }
                item.getParameters().add(new ResourceDetails(
                        null, item.getObjectId(), item.getName(),
                        item.getPath(), item.getOperations(), item.getMandatory(),
                        item.getInstanceType(), item.getValueRange(),
                        item.getValueType(), item.getUnits(), item.getDescription(),
                        instance.getValue(), instance.getInstanceId(), null, null
                ));
            }
        });

        resource.setItems(filteredItems);

        List<ResourceDetails> filteredParameters = parameters.stream()
                .filter(param -> param.getObjectId().equals(resource.getId()))
                .map(iotMapper::entityToResourceDetails)
                .collect(Collectors.toList());

        List<ResourceDetails> newParameters = new ArrayList<>();
        filteredParameters.forEach(param -> {
            Lwm2mInstanceEntity instance = instanceMap.get(param.getName());
            if (instance != null) {
                newParameters.add(new ResourceDetails(
                        null, param.getObjectId(), param.getName(),
                        param.getPath(), param.getOperations(), param.getMandatory(),
                        param.getInstanceType(), param.getValueRange(),
                        param.getValueType(), param.getUnits(), param.getDescription(),
                        instance.getValue(), instance.getInstanceId(), null, null
                ));
            }
        });

        resource.setParameters(filteredParameters);
        resource.getParameters().addAll(newParameters);
    }


    public FTPage<AbstractResource> getIotResourcesPage(final String token,
                                                        final IotResourceBody body) {
        jwtService.getSession(token);
        final List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(),
                Collections.singletonList(
                        FieldSort.builder()
                                .field("objectId")
                                .direction(OrderDirection.ASC)
                                .build()),
                "objectId");

        FTPage<AbstractResource> resourcePage = new FTPage<>();
        final List<AbstractResource> resources;
        final FTPageDetails pageDetails;
        final List<Page<ResourcesLwm2mEntity>> lwm2mResources =
                pageable.stream()
                        .map(body.getSearchParam() == null ?
                                lwm2mRepository::getAll :
                                p -> lwm2mRepository.getAllByParam(p, "%" + body.getSearchParam() + "%"))
                        .collect(Collectors.toList());
        resources = lwm2mResources.stream()
                .map(Page::getContent)
                .flatMap(entities -> entities.stream()
                        .map(iotMapper::entityToResourceLWM2MWithoutDescription))
                .collect(Collectors.toList());
        pageDetails = PageUtils.buildPageDetails(lwm2mResources);
        return resourcePage.toBuilder()
                .items(resources)
                .pageDetails(pageDetails)
                .build();
    }

    public ResourceDetailsResponse getIotResourceDetails(final String token, final IotResourceDetailBody body) {
        jwtService.getSession(token);
        // LWM2M:
        List<ResourceDetails> details = detailsLwm2mRepository.findAllByObjectId(body.getId())
                .stream()
                .map(iotMapper::entityToResourceDetails)
                .collect(Collectors.toList());
        return new ResourceDetailsResponse(details);
    }

    public boolean deleteIotResource(final String token, final DeleteIotResourceBody body) {
        jwtService.getSession(token);
        final List<Integer> ids = body.getIds();

        // LWM2M:
        final List<ResourcesLwm2mEntity> resources = lwm2mRepository.findAllById(ids);
        lwm2mRepository.deleteInBatch(resources);

        final List<ResourceDetailsLwm2mEntity> details = detailsLwm2mRepository.findAllByObjectIdIn(ids);
        detailsLwm2mRepository.deleteInBatch(details);

        return true;
    }

    public TaskIdsResponse addResourceFile(final String token, final MultipartFile file) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        // LWM2M:
        final String content;
        if (file == null) {
            throw new FriendlyIllegalArgumentException(FILE_FORMAT_NOT_SUPPORTED);
        }
        try {
            content = Base64.getEncoder().encodeToString(file.getBytes());
        } catch (IOException e) {
            throw new FriendlyIllegalArgumentException(FILE_FORMAT_NOT_SUPPORTED);
        }
        final FileDownloadRequest downloadRequest =
                FileDownloadRequest.builder()
                        .sendBytes(true)
                        .fileTypeId(fileTypeRepository.findAllByType("LWM2M Resource Definition")
                                .stream()
                                .map(FileTypeEntity::getId)
                                .findAny()
                                .orElse(null))
                        .fileContent(content)
                        .build();
        final Long transactionId =
                fileService.fileDownload(null, downloadRequest, clientType,
                        userService.getUser(session.getUserId(), session.getZoneId()), null, session, false);
        return new TaskIdsResponse(taskService.getTaskIds(transactionId));
    }

    public List<AbstractResource> getIotResources(Integer id) {

        final List<AbstractResource> resources;
        // LWM2M
        final List<Lwm2mResourceProjection> lwm2mResources =
                        lwm2mRepository.getAll(id);
        final List<ResourceDetailsLwm2mEntity> items =
                resourceDetailsLwm2mRepository.findAllWritableItems();
        final List<ResourceDetailsLwm2mEntity> parameters =
                resourceDetailsLwm2mRepository.findAllWritableParameters();
        final List<Lwm2mInstanceEntity> instances =
                iotMapper.objectToInstances(resourceDetailsLwm2mRepository.findAllInstances(id));


        List<Integer> instanceIds = getUniqueIds(instances);

        resources = lwm2mResources
                .stream()
                .map(iotMapper::objectsToResourceLWM2M)
                .collect(Collectors.toList());
        resources.forEach(r -> setMainItems(r, instanceIds));
        resources.forEach(r -> r.getItems().forEach(item -> setItemsAndParamsForSubResource(item, items, parameters, instances)));
        resources.forEach(r -> r.getItems().forEach(item -> fixResource(item, r)));
        return resources;

    }

    private void fixResource(ResourceDetailsItem item, AbstractResource r) {
        if(item.getParameters() != null && !item.getParameters().isEmpty() &&
                (item.getItems() == null || item.getItems().isEmpty())) {
            r.setItems(null);
            r.setParameters(item.getParameters());
        }
    }


    private void setItemsAndParamsForSubResource(ResourceDetailsItem resource,
                                                 List<ResourceDetailsLwm2mEntity> items,
                                                 List<ResourceDetailsLwm2mEntity> parameters,
                                                 List<Lwm2mInstanceEntity> instances) {
        resource.setItems(items
                .stream()
                .filter(item -> item.getObjectId().equals(resource.getObjectId()))
                .map(iotMapper::entityToResourceDetailsItem)
                .collect(Collectors.toList()));


        resource.getItems().forEach(item ->
                instances.forEach(instance -> {
                    if (item.getName().equals(instance.getName()) && instance.getInstanceId().equals(resource.getInstanceId())) {
                        item.setResourceInstanceId(instance.getResourceInstanceId());
                        item.setInstanceId(resource.getInstanceId());
                        if(item.getParameters() == null) {
                            item.setParameters(new ArrayList<>());
                        }
                        item.getParameters().add((new ResourceDetails(
                                null, item.getObjectId(), item.getName() + "." + item.getResourceInstanceId(),
                                item.getPath(), item.getOperations(), item.getMandatory(),
                                item.getInstanceType(), item.getValueRange(),
                                item.getValueType(), item.getUnits(), item.getDescription(),
                                instance.getValue(), instance.getInstanceId(), null, null
                        )));
                    }
                }));

        resource.setParameters(parameters
                .stream()
                .filter(param -> param.getObjectId().equals(resource.getObjectId()))
                .map(iotMapper::entityToResourceDetails)
                .collect(Collectors.toList()));

        resource.getItems().removeIf(item -> item.getParameters() == null
                || item.getParameters().isEmpty());

        List<ResourceDetails> newParameters = new ArrayList<>();

        resource.getParameters().forEach(param ->
                instances.forEach(instance -> {
                    if (param.getName().equals(instance.getName()) && instance.getInstanceId().equals(resource.getInstanceId())) {
                        newParameters.add(new ResourceDetails(
                                null, param.getObjectId(), param.getName(),
                                param.getPath(), param.getOperations(), param.getMandatory(),
                                param.getInstanceType(), param.getValueRange(),
                                param.getValueType(), param.getUnits(), param.getDescription(),
                                instance.getValue(), instance.getInstanceId(), null, null
                        ));
                    }
                }));

        resource.getParameters().removeIf(parameter -> parameter.getInstanceId() == null);

        resource.getParameters().addAll(newParameters);

    }

    private List<Integer> getUniqueIds(List<Lwm2mInstanceEntity> instances) {
        Set<Integer> uniqueIds = new HashSet<>();

        instances.forEach(instance -> uniqueIds.add(instance.getInstanceId()));

        return new ArrayList<>(uniqueIds);

    }

    private void setMainItems(AbstractResource resource, List<Integer> uniqueIds) {
        resource.setItems(new ArrayList<>());

        uniqueIds.forEach(uniqueId ->
                resource.getItems().add(new ResourceDetailsItem(null, resource.getId(), resource.getName() + "." + uniqueId.toString(),
                null, null, null, resource.getInstanceType(), null, null, null,
                resource.getDescription(), null, uniqueId, new ArrayList<>(), new ArrayList<>())));

    }
}
