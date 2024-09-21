package com.friendly.services.settings.userinterface;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.config.AbstractConfigItem;
import com.friendly.commons.models.settings.config.SelectorItem;
import com.friendly.commons.models.settings.response.AbstractConfigItemsResponse;
import com.friendly.commons.models.user.Session;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.settings.userinterface.orm.iotw.model.ClientInterfaceEntity;
import com.friendly.services.settings.userinterface.orm.iotw.model.InterfaceDescriptionEntity;
import com.friendly.services.settings.userinterface.orm.iotw.model.InterfaceItemEntity;
import com.friendly.services.settings.userinterface.orm.iotw.model.InterfaceSpecificDomain;
import com.friendly.services.uiservices.user.orm.iotw.model.UserEntity;
import com.friendly.services.settings.userinterface.orm.iotw.repository.ClientInterfaceRepository;
import com.friendly.services.settings.userinterface.orm.iotw.repository.InterfaceDescriptionRepository;
import com.friendly.services.settings.userinterface.orm.iotw.repository.InterfaceItemRepository;
import com.friendly.services.settings.userinterface.orm.iotw.repository.InterfaceSpecificDomainRepository;
import com.friendly.services.uiservices.user.orm.iotw.repository.UserRepository;
import com.friendly.services.settings.userinterface.mapper.ConfigMapper;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.friendly.commons.models.websocket.ActionType.UPDATE;
import static com.friendly.commons.models.websocket.SettingType.INTERFACE;
import static com.friendly.services.settings.userinterface.InterfaceItem.HIDE_FORGOT_PASSWORD;
import static com.friendly.services.settings.userinterface.InterfaceItem.PASSWORD_RESET_RETRY_COOLDOWN;

/**
 * Service that exposes the base functionality for interacting with {@link AbstractConfigItem} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterfaceService {

    @NonNull
    private final InterfaceSpecificDomainRepository interfaceSpecificDomainRepository;

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final ClientInterfaceRepository clientInterfaceRepository;

    @NonNull
    private final ConfigMapper configMapper;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final WsSender wsSender;

    @NonNull
    private final InterfaceItemRepository interfaceRepository;

    @NonNull
    private final InterfaceDescriptionRepository descriptionRepository;

    /**
     * Get User Interface Items
     *
     * @return user interface entities
     */
    public AbstractConfigItemsResponse getInterfaceItems(final String token) {
        final Session session = jwtService.getSession(token);
        final Long userId = session.getUserId();
        final Optional<UserEntity> user = userRepository.findById(userId);
        Integer domainId = user.get().getDomainId();

        return new AbstractConfigItemsResponse(this.getInterfaceItems(session.getClientType(), domainId));
    }

    public AbstractConfigItem getInterfaceItem(final Long userId, final String id, boolean justEncrypted) {
        return clientInterfaceRepository.getClientInterfaceItem(userId, id)
                .map(e -> configMapper.interfaceEntityToInterface(e, justEncrypted))
                .orElse(null);
    }

    public AbstractConfigItem getInterfaceItem(final ClientType clientType, final String id) {
        return clientInterfaceRepository.getClientInterfaceItem(clientType, id)
                .map(e -> configMapper.interfaceEntityToInterface(e, false))
                .orElse(null);
    }

    public Optional<String> getInterfaceValue(final ClientType clientType, final String interfaceId) {
        final Optional<String> interfaceValue = clientInterfaceRepository.getInterfaceValue(clientType, interfaceId);

        if (interfaceValue.isPresent()) {
            if(ConfigMapper.isBase64Encoded(interfaceValue.get())) {
                return Optional.of(new String(
                        Base64.getDecoder().decode(interfaceValue.get())));
            }
            return interfaceValue;
        } else {
            return clientInterfaceRepository.getDefaultInterfaceValue(interfaceId);
        }
    }

    public boolean getHideForgotPassword(ClientType clientType) {
        Optional<String> hideForgotPassword = getInterfaceValue(clientType,
                HIDE_FORGOT_PASSWORD.getValue());

        return hideForgotPassword.map(Boolean::valueOf)
                .orElse(false);
    }

    @Getter
    @AllArgsConstructor
    static class SelectorItemDto {
        private String value;
        private List<String> values;
    }

    /**
     * Update User Interface Items
     */
    @Transactional
    public AbstractConfigItemsResponse updateInterfaceItems(final String token,
                                                            final List<AbstractConfigItem> interfaceItems) throws JsonProcessingException {
        final Session session = jwtService.getSession(token);
        final Long userId = session.getUserId();
        final Optional<UserEntity> user = userRepository.findById(userId);
        Integer domainId = user.get().getDomainId();

        final ClientType clientType = session.getClientType();
        Set<AbstractConfigItem> changeSet = new HashSet<>();
        ClientInterfaceEntity entity;
        for (AbstractConfigItem interfaceItem : interfaceItems) {
            List<String> values;
            String value;
            final String domainSpecificValue;
            interfaceItem.setGroup(configMapper.groupNameToGroupId(interfaceItem.getGroup()));
            if (interfaceItem instanceof SelectorItem) {
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                value = ow.writeValueAsString(new SelectorItemDto(((SelectorItem) interfaceItem).getValue(),
                        ((SelectorItem) interfaceItem).getValues()));
                domainSpecificValue = ow.writeValueAsString(new SelectorItemDto(((SelectorItem) interfaceItem).getDomainSpecificValue(),
                        ((SelectorItem) interfaceItem).getValues()));
            } else {
                values = configMapper.getValueTypeProcessors()
                        .get(interfaceItem.getValueType())
                        .apply(interfaceItem);
                value = values.get(0);
                domainSpecificValue = values.get(1);
            }
            Optional<ClientInterfaceEntity> clientInterfaceEntity = clientInterfaceRepository
                    .getClientInterfaceItem(clientType, interfaceItem.getId());
            if(domainSpecificValue != null && domainId != 0) {
                Optional<InterfaceSpecificDomain> interfaceSpecificDomainOpt
                        = interfaceSpecificDomainRepository.findByInterfaceItemIdAndDomainIdAndClientType(
                                interfaceItem.getId(),
                                domainId,
                                clientType
                );
                Optional<InterfaceItemEntity> interfaceItemEntity = interfaceRepository.findById(interfaceItem.getId());
                if(clientInterfaceEntity.isPresent()) {
                    value = clientInterfaceEntity.get().getValue();
                } else if(interfaceItemEntity.isPresent()) {
                    value = interfaceItemEntity.get().getDefaultValue();
                }
                if(interfaceSpecificDomainOpt.isPresent()) {
                    InterfaceSpecificDomain interfaceSpecificDomain = interfaceSpecificDomainOpt.get();
                    interfaceSpecificDomain.setValue(domainSpecificValue);
                    interfaceSpecificDomainRepository.saveAndFlush(interfaceSpecificDomain);
                    changeSet.add(this.configMapper
                            .interfaceEntityToInterface(configMapper.toClientInterface(interfaceSpecificDomain, interfaceItem, value), false));
                } else {
                    InterfaceSpecificDomain specificDomainInterface = InterfaceSpecificDomain.builder()
                            .clientType(clientType)
                            .domainId(domainId)
                            .value(domainSpecificValue)
                            .interfaceItemId(interfaceItem.getId())
                            .build();
                    interfaceSpecificDomainRepository.save(specificDomainInterface);
                    changeSet.add(this.configMapper
                            .interfaceEntityToInterface(configMapper.toClientInterface(specificDomainInterface, interfaceItem, value), false));
                }
            }
            if(domainId != 0) {
               continue;
            }
            if (clientInterfaceEntity.isPresent()) {
                entity = clientInterfaceEntity.get();
                entity.setValue(encodeIfNeeded(value, interfaceItem.isEncrypted()));
                entity.setIsEncrypted(interfaceItem.isEncrypted());
                clientInterfaceRepository.updateClientInterfaceEntityValue(
                        interfaceItem.getId(), clientType, encodeIfNeeded(value, interfaceItem.isEncrypted()), interfaceItem.isEncrypted());
                changeSet.add(this.configMapper.interfaceEntityToInterface(entity, false));
            } else {
              entity =
                  ClientInterfaceEntity.builder()
                      .interfaceItem(interfaceRepository.getOne(interfaceItem.getId()))
                      .clientType(clientType)
                      .value(value)
                      .isEncrypted(interfaceItem.isEncrypted())
                      .build();
                if(!interfaceItem.isEncrypted()) {
                    clientInterfaceRepository.saveAndFlush(entity);
                }
        }

       wsSender.sendSettingEvent(clientType, UPDATE, INTERFACE,
               this.configMapper.interfaceEntityToInterface(entity, interfaceItem.isEncrypted()));
    }

        return new AbstractConfigItemsResponse(changeSet);
    }

    private String encodeIfNeeded(String value, boolean encrypted) {
        if(encrypted) {
            return Base64.getEncoder().encodeToString(value.getBytes());
        }
        return value;
    }

    public Optional<String> getInterfaceItemEntity(InterfaceItem interfaceItem) {
        return clientInterfaceRepository.findByInterfaceItem_Id(interfaceItem.getValue())
                .map(ClientInterfaceEntity::getValue);
    }

    public Integer getPasswordResetRetryCooldown(ClientType clientType) {
        return getInterfaceValue(clientType, PASSWORD_RESET_RETRY_COOLDOWN.getValue())
                .map(Integer::valueOf).orElse(0);
    }

    private Set<AbstractConfigItem> getInterfaceItems(ClientType clientType, Integer domainId) {
        return (Set) this.configMapper.interfaceEntitiesToInterfaces(this.interfaceRepository.findAll())
                .stream()
                .map((c) -> {
                    Optional<InterfaceSpecificDomain> specificDomain
                            = interfaceSpecificDomainRepository.findByInterfaceItemIdAndDomainIdAndClientType(
                            c.getId(),
                            domainId,
                            clientType
                    );

                    Optional<ClientInterfaceEntity> clientInterfaceItem = this.clientInterfaceRepository.getClientInterfaceItem(clientType, c.getId());
                    if(specificDomain.isPresent() && clientInterfaceItem.isPresent()) {
                        clientInterfaceItem.get().setDomainSpecificValue(specificDomain.get().getValue());
                    } else if(specificDomain.isPresent() && !clientInterfaceItem.isPresent()) {
                        configMapper.setDomainSpecificValue(c, specificDomain.get().getValue());
                    }
                    return clientInterfaceItem.isPresent() ? this.configMapper.interfaceEntityToInterface((ClientInterfaceEntity) clientInterfaceItem.get(), false) : c;
                }).map((c) -> {
                    c.setDescription(this.getDescription(c.getId()));
                    return c;
                }).collect(Collectors.toCollection(LinkedHashSet::new));
    }


    private String getDescription(String id) {
        return (String) this.descriptionRepository.findFirstByInterfaceDescriptionIdAndLocaleId(id, "EN").map(InterfaceDescriptionEntity::getDescription).orElse((String) null);
    }
}
