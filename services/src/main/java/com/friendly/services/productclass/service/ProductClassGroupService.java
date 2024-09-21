package com.friendly.services.productclass.service;

import com.friendly.commons.models.FTPage;
import com.friendly.commons.models.device.DeviceModel;
import com.friendly.commons.models.device.Manufacturer;
import com.friendly.commons.models.device.ProtocolTypeRequest;
import com.friendly.commons.models.device.RequestPageInfo;
import com.friendly.commons.models.device.UnusedModelsResponse;
import com.friendly.commons.models.device.response.ManufacturersResponse;
import com.friendly.commons.models.request.LongIdsRequest;
import com.friendly.services.device.info.utils.DeviceUtils;
import com.friendly.services.device.parameterstree.orm.acs.repository.RetrieveModeRepository;
import com.friendly.services.filemanagement.orm.acs.repository.FilesFtpRepository;
import com.friendly.services.management.events.orm.acs.repository.EventMonitoringRepository;
import com.friendly.services.management.profiles.orm.acs.repository.ProfileFileRepository;
import com.friendly.services.productclass.orm.acs.repository.ProductClassGroupRepository;
import com.friendly.services.qoemonitoring.orm.acs.repository.QoeMonitoringRepository;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.device.mapper.ModelMapper;
import com.friendly.services.productclass.orm.acs.model.ProductClassGroupEntity;
import com.friendly.services.infrastructure.utils.PageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductClassGroupService {

    @NonNull
    private final JwtService jwtService;
    @NonNull
    private final ProductClassGroupRepository productClassGroupRepository;
    @NonNull
    private final ProfileFileRepository profileFileRepository;
    @NonNull
    private final QoeMonitoringRepository qoeMonitoringRepository;
    @NonNull
    private final EventMonitoringRepository eventMonitoringRepository;
    @NonNull
    private final FilesFtpRepository filesFtpRepository;
    @NonNull
    private final ModelMapper modelMapper;

    private final RetrieveModeRepository retrieveModeRepository;

    public FTPage<UnusedModelsResponse> getUnusedModels(String token, RequestPageInfo body) {
        jwtService.getSession(token);

        List<Pageable> pageable = PageUtils.createPageRequest(body.getPageNumbers(), body.getPageSize(),
                body.getSorts(), "id");

        List<Long> allModelIds = productClassGroupRepository.findAllIds();

        List<Long> unusedModelIds = removeUsedIdsFromList(allModelIds, getUsedModelIds());

        List<Long> usedCpeIds = productClassGroupRepository.getUsedId(unusedModelIds);

        unusedModelIds = removeUsedIdsFromList(unusedModelIds, usedCpeIds);

        List<Page<ProductClassGroupEntity>> entities = new ArrayList<>();

        if (unusedModelIds.size() != 0) {
            List<Long> finalUnusedModelIds = unusedModelIds;
            entities = pageable.stream()
                    .map(p -> productClassGroupRepository.findAll(finalUnusedModelIds, p))
                    .collect(Collectors.toList());
        }

        final List<UnusedModelsResponse> modelsResponses = entities.stream()
                .map(Page::getContent)
                .flatMap(Collection::stream)
                .map(modelMapper::productClassGroupToUnusedModelsResponse)
                .collect(Collectors.toList());

        final FTPage<UnusedModelsResponse> result = new FTPage<>();
        return result.toBuilder()
                .items(modelsResponses)
                .pageDetails(PageUtils.buildPageDetails(entities))
                .build();
    }

    private List<Long> removeUsedIdsFromList(List<Long> allIds, List<Long> usedIds) {
        return allIds.stream()
                .filter(id -> !usedIds.contains(id))
                .collect(Collectors.toList());
    }


    private List<Long> getUsedModelIds() {
        List<Long> models = new ArrayList<>();
        models.addAll(profileFileRepository.getUsedGroupId());
        models.addAll(qoeMonitoringRepository.getUsedGroupId());
        models.addAll(eventMonitoringRepository.getUsedGroupId());
        models.addAll(filesFtpRepository.getUsedGroupId());
        return models;
    }

    public void deleteUnusedModels(String token, LongIdsRequest body) {
        jwtService.getSession(token);
        productClassGroupRepository.deleteUnusedModels(body.getIds());
    }

    public ManufacturersResponse getManufacturerNames(
            final String token, final ProtocolTypeRequest protocolType) {
        jwtService.getUserIdByHeaderAuth(token);

        final Map<String, Set<ProductClassGroupEntity>> productClassModelMap =
                productClassGroupRepository
                        .getProductClassesByProtocolId(
                                DeviceUtils.convertProtocolTypeToId(protocolType.getProtocolType()))
                        .stream()
                        .collect(
                                Collectors.groupingBy(
                                        ProductClassGroupEntity::getManufacturerName, Collectors.toSet()));

        List<Manufacturer> manufacturers =
                productClassModelMap.keySet().stream()
                        .filter(m -> !m.equalsIgnoreCase("DEFAULT"))
                        .map(m -> buildManufacturer(m, productClassModelMap.get(m)))
                        .sorted(Comparator.comparing(Manufacturer::getName, String.CASE_INSENSITIVE_ORDER))
                        .collect(Collectors.toList());
        return new ManufacturersResponse(manufacturers);
    }

    public ManufacturersResponse getRetrieveManufacturers(final String token) {
        jwtService.getUserIdByHeaderAuth(token);

        List<Integer> retrieveModeRepositoryIds = retrieveModeRepository.getIds();

        final Map<String, Set<ProductClassGroupEntity>> productClassModelMap =
                productClassGroupRepository.getProductClassesByTR069Protocol().stream()
                        .filter(m -> !retrieveModeRepositoryIds.contains(m.getId()))
                        .collect(
                                Collectors.groupingBy(
                                        ProductClassGroupEntity::getManufacturerName, Collectors.toSet()));

        List<Manufacturer> manufacturers =
                productClassModelMap.keySet().stream()
                        .filter(m -> !m.equalsIgnoreCase("DEFAULT"))
                        .map(m -> buildManufacturer(m, productClassModelMap.get(m)))
                        .sorted(Comparator.comparing(Manufacturer::getName, String.CASE_INSENSITIVE_ORDER))
                        .collect(Collectors.toList());
        return new ManufacturersResponse(manufacturers);
    }

    public ManufacturersResponse getWhiteListManufacturers(final String token) {
        jwtService.getUserIdByHeaderAuth(token);

        final Map<String, Set<ProductClassGroupEntity>> productClassModelMap =
                productClassGroupRepository.getProductClassesByTR069Protocol().stream()
                        .collect(
                                Collectors.groupingBy(
                                        ProductClassGroupEntity::getManufacturerName, Collectors.toSet()));

        List<Manufacturer> manufacturers =
                productClassModelMap.keySet().stream()
                        .map(m -> buildManufacturer(m, productClassModelMap.get(m)))
                        .sorted(Comparator.comparing(Manufacturer::getName, String.CASE_INSENSITIVE_ORDER))
                        .collect(Collectors.toList());
        return new ManufacturersResponse(manufacturers);
    }

    private Manufacturer buildManufacturer(
            final String manufacturerName, Set<ProductClassGroupEntity> models) {
        return Manufacturer.builder()
                .name(manufacturerName)
                .models(
                        buildDeviceModels(
                                models.stream()
                                        .filter(e -> StringUtils.hasText(e.getModel()))
                                        .collect(
                                                Collectors.toMap(
                                                        ProductClassGroupEntity::getId, ProductClassGroupEntity::getModel,
                                                        (s, s2) -> s2))))
                .ouis(
                        getProductClassGroupEntityIds(models).stream()
                                .map(productClassGroupRepository::getOUIsByProductClassId)
                                .flatMap(List::stream)
                                .collect(Collectors.toList()))
                .build();
    }

    private List<Long> getProductClassGroupEntityIds(Set<ProductClassGroupEntity> manufacturer) {
        return manufacturer.stream().map(ProductClassGroupEntity::getId).collect(Collectors.toList());
    }

    private List<DeviceModel> buildDeviceModels(Map<Long, String> productClass) {
        return productClass.keySet().stream()
                .map(id -> buildDeviceModel(id, productClass.get(id)))
                .sorted(Comparator.comparing(DeviceModel::getName))
                .collect(Collectors.toList());
    }

    private DeviceModel buildDeviceModel(final Long productClassId, String productClassName) {
        return DeviceModel.builder().id(productClassId).name(productClassName).build();
    }

}
