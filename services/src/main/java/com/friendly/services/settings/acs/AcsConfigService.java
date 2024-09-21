package com.friendly.services.settings.acs;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.settings.config.AbstractConfigItem;
import com.friendly.commons.models.settings.config.BooleanItem;
import com.friendly.commons.models.settings.config.IntegerItem;
import com.friendly.commons.models.settings.config.StringItem;
import com.friendly.commons.models.settings.request.ConfigItemRequest;
import com.friendly.commons.models.settings.response.AbstractConfigItemsListResponse;
import com.friendly.commons.models.user.Session;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.settings.userinterface.mapper.ConfigMapper;
import com.ftacs.ACSWebService;
import com.ftacs.ConfigEntry;
import com.ftacs.Exception_Exception;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.ACS_EXCEPTION;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AcsConfigService {

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final ConfigMapper configMapper;


    public AbstractConfigItemsListResponse getAcsConfig(final String token) {
        final Session session = jwtService.getSession(token);

        List<AbstractConfigItem> items = AcsProvider.getAcsWebService(session.getClientType())
                .getAcsConfiguration()
                .getEntry()
                .stream()
                .map(this::configEntryToInterfaceItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new AbstractConfigItemsListResponse(items);
    }

    public boolean setAcsConfig(final String token, final ConfigItemRequest request) {
        final Session session = jwtService.getSession(token);

        final ACSWebService acsWebService = AcsProvider.getAcsWebService(session.getClientType());
        request.getItems().forEach(config -> {
            try {
                acsWebService.setACSParam(config.getId(), configMapper.getValueTypeProcessors()
                        .get(config.getValueType())
                        .apply(config).get(0));
            } catch (Exception_Exception e) {
                throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
            }
        });

        return true;
    }

    public String getAcsConfigParameterValue(final String token, final String name) {
        final Session session = jwtService.getSession(token);
        try {
            return AcsProvider.getAcsWebService(session.getClientType())
                    .getACSParam(name);
        } catch (Exception_Exception e) {
            throw new FriendlyIllegalArgumentException(ACS_EXCEPTION, e.getMessage());
        }
    }

    private AbstractConfigItem configEntryToInterfaceItem(final ConfigEntry configEntry) {
        final String configType = configEntry.getType();
        final String value = configEntry.getValue();
        AbstractConfigItem item;
        switch (configType) {
            case "Bool":
                final boolean boolValue;
                if (value.equals("1")) {
                    boolValue = true;
                } else if (value.equals("0")) {
                    boolValue = false;
                } else {
                    boolValue = Boolean.parseBoolean(value);
                }
                item = BooleanItem.builder()
                        .id(configEntry.getName())
                        .description(configEntry.getComment())
                        .value(boolValue)
                        .required(false)
                        .build();
            case "Int":
                item = IntegerItem.builder()
                        .id(configEntry.getName())
                        .description(configEntry.getComment())
                        .value(Integer.parseInt(value))
                        .required(false)
                        .build();
            default: // "URL" "Str" "ListByComa"
                item = StringItem.builder()
                        .id(configEntry.getName())
                        .description(configEntry.getComment())
                        .value(value)
                        .required(false)
                        .build();

        }
        switch (configEntry.getName()) {
            case "stunRedirectPort":
            case "xmppServerPort":
                item.setRequired(true);
        }
        return item;
    }

}
