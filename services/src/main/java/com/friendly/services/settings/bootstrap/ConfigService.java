package com.friendly.services.settings.bootstrap;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.settings.acs.IotConfigTab;
import com.friendly.commons.models.settings.acs.ProtocolTabType;
import com.friendly.commons.models.settings.config.property.AbstractConfigProperty;
import com.friendly.commons.models.settings.config.property.UpdateIotPropertyRequest;
import com.friendly.commons.models.settings.iot.response.AbstractConfigPropertiesResponse;
import com.friendly.commons.models.settings.iot.response.IotConfigTabsResponse;
import com.friendly.commons.models.user.Session;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.settings.acs.orm.acs.model.IotConfigGroupEntity;
import com.friendly.services.settings.acs.orm.acs.model.IotConfigPropertyEntity;
import com.friendly.services.settings.acs.orm.acs.repository.IotConfigGroupRepository;
import com.friendly.services.settings.acs.orm.acs.repository.IotConfigPropertyRepository;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.settings.domain.DomainService;
import com.friendly.services.settings.userinterface.mapper.ConfigMapper;
import com.ftacs.ACSWebService;
import com.ftacs.ConfigurationParameter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.friendly.commons.models.settings.acs.ProtocolTabType.*;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.CAN_NOT_OVERRIDE_PROPERTY;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.PROPERTY_NOT_FOUND;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigService {

    private final static IotConfigTab BOOTSTRAP_TAB = IotConfigTab.builder()
            .name("Bootstrap")
            .items(Arrays.asList(IotConfigTab.builder()
                            .name("Configurations")
                            .build(),
                    IotConfigTab.builder()
                            .name("Log")
                            .build()))
            .build();
    private final static IotConfigTab RESOURCE_TAB = IotConfigTab.builder().name("Resource").build();
    private final static IotConfigTab SECURITY_TAB = IotConfigTab.builder().name("Security").build();

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final ConfigMapper configMapper;

    @NonNull
    private final DomainService domainService;

    @NonNull
    private final IotConfigGroupRepository configGroupRepository;

    @NonNull
    private final IotConfigPropertyRepository configPropertyRepository;


    public IotConfigTabsResponse getIotConfigTabs(final String token, final ProtocolTabType protocolTabType) {
        jwtService.getSession(token);

        final List<IotConfigTab> tabs = new ArrayList<>();
        switch (protocolTabType) {
            case LWM2M:
                tabs.addAll(Arrays.asList(BOOTSTRAP_TAB, RESOURCE_TAB, SECURITY_TAB));
                tabs.addAll(getTabsByConfigGroup(LWM2M.name()));
                break;
            case MQTT:
                tabs.add(RESOURCE_TAB);
                tabs.addAll(getTabsByConfigGroup(MQTT.name()));
                break;
            case USP:
                tabs.add(RESOURCE_TAB);
                tabs.addAll(getTabsByConfigGroup(USP.name()));
                break;
            case ExtSystem:
                tabs.addAll(getTabsByConfigGroup(ExtSystem.name()));
                break;
        }
        return new IotConfigTabsResponse(tabs);
    }

    public AbstractConfigPropertiesResponse getIotConfigProperties(final String token, final Integer groupId) {
        final Session session = jwtService.getSession(token);
        final Integer currentDomainId = getCurrentDomainId(session);
        final List<Integer> childDomainIds = new ArrayList<>();
        if (currentDomainId != 0) {
            childDomainIds.addAll(domainService.getChildDomainIds(currentDomainId));
        }
        List<IotConfigPropertyEntity> config =
                configPropertyRepository.findAllByGroupIdAndDomainId(groupId, currentDomainId);
        if (currentDomainId != null && config.isEmpty()) {
            config = getIotConfigPropertiesIfEmpty(groupId, currentDomainId, config);
        }
        List<AbstractConfigProperty> properties = config.stream()
                .map(p -> configMapper.iotConfigEntityToConfigProperty(p, currentDomainId, childDomainIds))
                .collect(Collectors.toList());
        return new AbstractConfigPropertiesResponse(properties);
    }

    private List<IotConfigPropertyEntity> getIotConfigPropertiesIfEmpty(final Integer groupId,
                                                                        final Integer domainId,
                                                                        final List<IotConfigPropertyEntity> config) {
        if (config.isEmpty()) {
            final Integer parentDomainId = domainService.getParentDomainId(domainId);
            final List<IotConfigPropertyEntity> newConfig = configPropertyRepository.findAllByGroupIdAndDomainId(
                    groupId, parentDomainId.equals(-1) ? null : parentDomainId);
            if (parentDomainId.equals(-1) && newConfig.isEmpty()) {
                return newConfig;
            } else {
                return getIotConfigPropertiesIfEmpty(groupId, parentDomainId, newConfig);
            }
        }
        return config;
    }

    public void updateIotConfigProperties(final String token, final UpdateIotPropertyRequest request) {
        final Session session = jwtService.getSession(token);

        final Integer currentDomainId = getCurrentDomainId(session);

        final ACSWebService acsWebService = AcsProvider.getAcsWebService(session.getClientType());
        final List<ConfigurationParameter> parameters =
                request.getItems()
                        .stream()
                        .map(property -> {
                            final IotConfigPropertyEntity currentProperty =
                                    configPropertyRepository.findById(property.getId())
                                            .orElseThrow(() -> new FriendlyEntityNotFoundException(
                                                    PROPERTY_NOT_FOUND, property.getId()));

                            final boolean canUpdatable = currentDomainId.equals(0) ||
                                    !currentDomainId.equals(0) && currentProperty.getDomainId().equals(currentDomainId);
                            final boolean canOverridable = !currentDomainId.equals(0)
                                    && !currentProperty.getDomainId().equals(currentDomainId)
                                    && currentProperty.getOverridable() != null && currentProperty.getOverridable();

                            final ConfigurationParameter parameter = new ConfigurationParameter();
                            if (canUpdatable) {
                                parameter.setId(property.getId());
                                parameter.setValue(property.getValue());
                            } else if (canOverridable) {
                                parameter.setId(property.getId());
                                parameter.setValue(property.getValue());
                                parameter.setLocationId(currentDomainId);
                            } else {
                                throw new FriendlyIllegalArgumentException(CAN_NOT_OVERRIDE_PROPERTY,
                                        currentProperty.getName());
                            }
                            return parameter;
                        })
                        .collect(Collectors.toList());

        parameters.forEach(acsWebService::saveOrUpdateConfigurationParameter);
    }

    private Integer getCurrentDomainId(Session session) {
        Optional<Integer> result = domainService.getDomainIdByUserId(session.getUserId());
        Integer domainId = result.isPresent() ? result.get() : 0;
        final Integer currentDomainId;
        if (domainId == null) {
            currentDomainId = 0;
        } else {
            currentDomainId = domainId;
        }
        return currentDomainId;
    }

    private List<IotConfigTab> getTabsByConfigGroup(final String name) {
        final Map<String, List<IotConfigGroupEntity>> configEntities =
                configGroupRepository.findAllByNameLike(name + "%")
                        .stream()
                        .filter(e -> configPropertyRepository.isExists(e.getId()))
                        .collect(Collectors.groupingBy(config -> getName(config, name)));

        return getConfigTabs(configEntities);
    }

    private List<IotConfigTab> getConfigTabs(final Map<String, List<IotConfigGroupEntity>> configEntities) {
        return configEntities.keySet()
                .stream()
                .map(key -> getConfigTab(configEntities, key))
                .sorted(Comparator.comparing(IotConfigTab::getName))
                .collect(Collectors.toList());
    }

    private String getName(final IotConfigGroupEntity config, final String name) {
        String separator = !config.getName().startsWith(name) ? "." + name + "." : name + ".";
        final String s = StringUtils.substringAfter(config.getName(), separator);

        return s.contains(".") ? StringUtils.substringBefore(s, ".") : s;
    }

    private IotConfigTab getConfigTab(final Map<String, List<IotConfigGroupEntity>> configEntities,
                                      final String key) {
        final Map<String, List<IotConfigGroupEntity>> configMap =
                configEntities.get(key)
                        .stream()
                        .collect(Collectors.groupingBy(config -> getName(config, key)));

        final IotConfigTab.IotConfigTabBuilder tabBuilder = IotConfigTab.builder()
                .name(key);
        if (configMap.containsKey("")) {
            final Optional<IotConfigGroupEntity> iotConfigGroupEntity = configMap.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .findAny();
            tabBuilder.id(iotConfigGroupEntity.map(IotConfigGroupEntity::getId).orElse(null));
            tabBuilder.fullName(iotConfigGroupEntity.map(IotConfigGroupEntity::getProgramName).orElse(null));
            tabBuilder.description(iotConfigGroupEntity.map(IotConfigGroupEntity::getDescription).orElse(null));
        } else {
            tabBuilder.items(getConfigTabs(configMap));
        }
        return tabBuilder.build();
    }

}
