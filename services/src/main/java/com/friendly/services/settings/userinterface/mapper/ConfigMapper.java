package com.friendly.services.settings.userinterface.mapper;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.CAN_NOT_SERIALIZE_OBJECT;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.settings.config.*;
import com.friendly.commons.models.settings.config.property.*;
import com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum;
import com.friendly.services.settings.acs.orm.acs.model.IotConfigPropertyEntity;
import com.friendly.services.settings.userinterface.orm.iotw.model.ClientInterfaceEntity;
import com.friendly.services.settings.userinterface.orm.iotw.model.InterfaceDescriptionEntity;
import com.friendly.services.settings.userinterface.orm.iotw.model.InterfaceItemEntity;
import com.friendly.services.settings.userinterface.orm.iotw.model.InterfaceSpecificDomain;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfigMapper {

    @NonNull
    private final ObjectMapper mapper;

    @Getter
    private final Map<ValueType, Function<AbstractConfigItem, List<String>>> valueTypeProcessors =
            new EnumMap<>(ValueType.class);

    @PostConstruct
    public void init() {
        valueTypeProcessors.put(ValueType.BOOLEAN, this::getValueFromBoolean);
        valueTypeProcessors.put(ValueType.INTEGER, this::getValueFromInteger);
        valueTypeProcessors.put(ValueType.STRING, this::getValueFromString);
        valueTypeProcessors.put(ValueType.LIST_INTEGER, this::getValueFromListInteger);
        valueTypeProcessors.put(ValueType.LIST_STRING, this::getValueFromListString);
        valueTypeProcessors.put(ValueType.SELECTOR, this::getValueFromSelector);
        valueTypeProcessors.put(ValueType.LIST_PARAMETER, this::getValueFromListParameters);
        valueTypeProcessors.put(ValueType.LIST_MANUFACTURER, this::getValueFromManufacturer);
    }

    private List<String> getValueFromBoolean(final AbstractConfigItem interfaceItem) {
        final BooleanItem item = (BooleanItem) interfaceItem;
        List<String> values = new ArrayList<>();
        values.add(String.valueOf(item.getValue()));
        values.add(String.valueOf(item.getDomainSpecificValue()));
        return values;
    }

    private List<String> getValueFromInteger(final AbstractConfigItem interfaceItem) {
        final IntegerItem item = (IntegerItem) interfaceItem;
        List<String> values = new ArrayList<>();
        values.add(String.valueOf(item.getValue()));
        values.add(String.valueOf(item.getDomainSpecificValue()));
        return values;
    }

    private List<String> getValueFromString(final AbstractConfigItem interfaceItem) {
        final StringItem item = (StringItem) interfaceItem;
        List<String> values = new ArrayList<>();
        values.add(item.getValue());
        values.add(item.getDomainSpecificValue());
        return values;
    }

    @SneakyThrows
    private List<String> getValueFromListInteger(final AbstractConfigItem interfaceItem) {
        final ListIntegerItem item = (ListIntegerItem) interfaceItem;
        List<String> values = new ArrayList<>();
        values.add(mapper.writeValueAsString(item.getValue()));
        values.add(mapper.writeValueAsString(item.getDomainSpecificValue()));
        return values;
    }

    @SneakyThrows
    private List<String> getValueFromListString(final AbstractConfigItem interfaceItem) {
        final ListStringItem item = (ListStringItem) interfaceItem;
        List<String> values = new ArrayList<>();
        values.add(mapper.writeValueAsString(item.getValue()));
        values.add(mapper.writeValueAsString(item.getDomainSpecificValue()));
        return values;
    }

    @SneakyThrows
    private List<String> getValueFromSelector(final AbstractConfigItem interfaceItem) {
        final SelectorItem item = (SelectorItem) interfaceItem;
        List<String> values = new ArrayList<>();
        values.add(item.getValue());
        values.add(item.getDomainSpecificValue());
        return values;
    }

    @SneakyThrows
    private List<String> getValueFromListParameters(final AbstractConfigItem interfaceItem) {
        final ListParameterItem item = (ListParameterItem) interfaceItem;
        List<String> values = new ArrayList<>();
        values.add(mapper.writeValueAsString(item.getValue()));
        values.add(mapper.writeValueAsString(item.getDomainSpecificValue()));
        return values;
    }

    @SneakyThrows
    private List<String> getValueFromManufacturer(final AbstractConfigItem interfaceItem) {
        final ManufacturerItem item = (ManufacturerItem) interfaceItem;
        List<String> values = new ArrayList<>();
        values.add(mapper.writeValueAsString(item.getValue()));
        values.add(mapper.writeValueAsString(item.getDomainSpecificValue()));
        return values;
    }

    public Set<AbstractConfigItem> interfaceEntitiesToInterfaces(final List<InterfaceItemEntity> interfaceItemEntities) {
        return interfaceItemEntities == null ?
                Collections.EMPTY_SET
                : (Set) interfaceItemEntities.stream()
                .map(this::interfaceEntityToInterface)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public AbstractConfigItem interfaceEntityToInterface(final InterfaceItemEntity interfaceItemEntity) {
        try {
            if(interfaceItemEntity.getDomainSpecificValue() == null) {
                interfaceItemEntity.setDomainSpecificValue(interfaceItemEntity.getDefaultValue());
            }
            switch (interfaceItemEntity.getValueType()) {
                case INTEGER:
                    return IntegerItem.builder()
                            .id(interfaceItemEntity.getId())
                            .value(Integer.valueOf(interfaceItemEntity.getDefaultValue()))
                            .domainSpecificValue(Integer.valueOf(interfaceItemEntity.getDomainSpecificValue()))
                            .required(interfaceItemEntity.getIsRequired() != null && interfaceItemEntity.getIsRequired())
                            .group(convertGroupIdToGroup(interfaceItemEntity.getGroupId() != null
                                    ? interfaceItemEntity.getGroupId() : "others"))
                            .build();
                case BOOLEAN:
                    return BooleanItem.builder()
                            .id(interfaceItemEntity.getId())
                            .value(Boolean.valueOf(interfaceItemEntity.getDefaultValue()))
                            .domainSpecificValue(Boolean.valueOf(interfaceItemEntity.getDomainSpecificValue()))
                            .required(interfaceItemEntity.getIsRequired() != null && interfaceItemEntity.getIsRequired())
                            .group(convertGroupIdToGroup(interfaceItemEntity.getGroupId() != null
                                    ? interfaceItemEntity.getGroupId() : "others"))
                            .build();
                case STRING:
                    return StringItem.builder()
                            .id(interfaceItemEntity.getId())
                            .value(interfaceItemEntity.getDefaultValue())
                            .domainSpecificValue(interfaceItemEntity.getDomainSpecificValue())
                            .required(interfaceItemEntity.getIsRequired() != null && interfaceItemEntity.getIsRequired())
                            .group(convertGroupIdToGroup(interfaceItemEntity.getGroupId() != null
                                    ? interfaceItemEntity.getGroupId() : "others"))
                            .build();
                case LIST_STRING:
                    return ListStringItem.builder()
                            .id(interfaceItemEntity.getId())
                            .value((List) this.mapper.readValue(interfaceItemEntity.getDefaultValue(), new TypeReference<List<String>>() {
                            }))
                            .domainSpecificValue((List) this.mapper.readValue(interfaceItemEntity.getDomainSpecificValue(), new TypeReference<List<String>>() {
                            }))
                            .required(interfaceItemEntity.getIsRequired() != null && interfaceItemEntity.getIsRequired())
                            .group(convertGroupIdToGroup(interfaceItemEntity.getGroupId() != null
                                    ? interfaceItemEntity.getGroupId() : "others"))
                            .build();
                case LIST_INTEGER:
                    return ListIntegerItem.builder()
                            .id(interfaceItemEntity.getId())
                            .value((List) this.mapper.readValue(interfaceItemEntity.getDefaultValue(), new TypeReference<List<Integer>>() {
                            }))
                            .domainSpecificValue((List) this.mapper.readValue(interfaceItemEntity.getDomainSpecificValue(), new TypeReference<List<Integer>>() {
                            }))
                            .required(interfaceItemEntity.getIsRequired())
                            .group(convertGroupIdToGroup(interfaceItemEntity.getGroupId() != null
                                    ? interfaceItemEntity.getGroupId() : "others"))
                            .build();
                case SELECTOR:
                    Selector selector = this.mapper.readValue(interfaceItemEntity.getDefaultValue(), Selector.class);
                    Selector selectorSpecific = this.mapper.readValue(interfaceItemEntity.getDomainSpecificValue(), Selector.class);
                    return SelectorItem.builder()
                            .id(interfaceItemEntity.getId())
                            .value(selector.getValue())
                            .domainSpecificValue(selectorSpecific.getValue())
                            .values(selector.getValues().stream().sorted().collect(Collectors.toList()))
                            .required(interfaceItemEntity.getIsRequired() != null && interfaceItemEntity.getIsRequired())
                            .group(convertGroupIdToGroup(interfaceItemEntity.getGroupId() != null
                                    ? interfaceItemEntity.getGroupId() : "others"))
                            .build();
                case LIST_PARAMETER:
                    return ListParameterItem.builder()
                            .id(interfaceItemEntity.getId())
                            .value((ListParameter) this.mapper.readValue(interfaceItemEntity.getDefaultValue(), ListParameter.class))
                            .domainSpecificValue((ListParameter) this.mapper.readValue(interfaceItemEntity.getDomainSpecificValue(), ListParameter.class))
                            .required(interfaceItemEntity.getIsRequired() != null && interfaceItemEntity.getIsRequired())
                            .group(convertGroupIdToGroup(interfaceItemEntity.getGroupId() != null
                                    ? interfaceItemEntity.getGroupId() : "others"))
                            .build();
                case LIST_MANUFACTURER:
                    return ManufacturerItem.builder()
                            .id(interfaceItemEntity.getId())
                            .value((List) this.mapper.readValue(interfaceItemEntity.getDefaultValue(), new TypeReference<List<Manufacturer>>() {
                            }))
                            .domainSpecificValue((List) this.mapper.readValue(interfaceItemEntity.getDomainSpecificValue(), new TypeReference<List<Manufacturer>>() {
                            }))
                            .required(interfaceItemEntity.getIsRequired() != null && interfaceItemEntity.getIsRequired())
                            .group(convertGroupIdToGroup(interfaceItemEntity.getGroupId() != null
                                    ? interfaceItemEntity.getGroupId() : "others"))
                            .build();
                default:
                    return null;
            }
        } catch (IOException var3) {
            throw new FriendlyIllegalArgumentException(ServicesErrorRegistryEnum.CAN_NOT_SERIALIZE_OBJECT, new Object[]{"interface item"});
        }
    }

    private String convertGroupIdToGroup(String groupId) {
        switch (groupId) {
            case "auth":
                return "Authentication";
            case "diagnostics":
                return "Diagnostics";
            case "isp":
                return "ISP";
            case "timeouts":
                return "Generic timeouts";
            case "pass policy":
                return "Password policy";
            default:
                return "Others settings";
        }
    }

    public AbstractConfigItem interfaceEntityToInterface(final ClientInterfaceEntity clientInterfaceEntity, boolean justEncrypted) {
        try {
            if (!justEncrypted && Boolean.TRUE.equals(clientInterfaceEntity.getIsEncrypted())
                    && isBase64Encoded(clientInterfaceEntity.getValue())) {
                clientInterfaceEntity.setValue(new String(
                        Base64.getDecoder().decode(clientInterfaceEntity.getValue())));
            }
            switch (clientInterfaceEntity.getInterfaceItem().getValueType()) {
                case INTEGER:
                    boolean flag = StringUtils.isBlank(clientInterfaceEntity.getValue()) ||
                            clientInterfaceEntity.getValue() == null || clientInterfaceEntity.getValue().equals("null");
                    boolean flag1 = StringUtils.isBlank(clientInterfaceEntity.getDomainSpecificValue()) ||
                            clientInterfaceEntity.getDomainSpecificValue() == null ||
                            clientInterfaceEntity.getDomainSpecificValue().equals("null");
                    return IntegerItem.builder()
                            .id(clientInterfaceEntity.getInterfaceItem().getId())
                            .required(clientInterfaceEntity.getInterfaceItem().getIsRequired() != null && clientInterfaceEntity.getInterfaceItem().getIsRequired())
                            .value(flag ? null
                                    : Integer.valueOf(clientInterfaceEntity.getValue()))
                            .domainSpecificValue(flag1 ? null
                                    : Integer.valueOf(clientInterfaceEntity.getDomainSpecificValue()))
                            .description(clientInterfaceEntity.getInterfaceItem()
                                    .getInterfaceDescriptions()
                                    .get(0).getDescription())
                            .isEncrypted(clientInterfaceEntity.getIsEncrypted() != null ? clientInterfaceEntity.getIsEncrypted() : false)
                            .group(convertGroupIdToGroup(clientInterfaceEntity.getInterfaceItem().getGroupId() != null
                                    ? clientInterfaceEntity.getInterfaceItem().getGroupId() : "others"))
                            .build();

                case BOOLEAN:
                    return BooleanItem.builder()
                            .id(clientInterfaceEntity.getInterfaceItem().getId())
                            .required(clientInterfaceEntity.getInterfaceItem().getIsRequired() != null && clientInterfaceEntity.getInterfaceItem().getIsRequired())
                            .value(!StringUtils.isBlank(clientInterfaceEntity.getValue()) && Boolean.valueOf(clientInterfaceEntity.getValue()))
                            .domainSpecificValue(clientInterfaceEntity.getDomainSpecificValue() == null ? null
                                    : !StringUtils.isBlank(clientInterfaceEntity.getDomainSpecificValue()) && Boolean.valueOf(clientInterfaceEntity.getDomainSpecificValue()))
                            .description(clientInterfaceEntity.getInterfaceItem()
                                    .getInterfaceDescriptions()
                                    .get(0).getDescription())
                            .isEncrypted(clientInterfaceEntity.getIsEncrypted() != null ? clientInterfaceEntity.getIsEncrypted() : false)
                            .group(convertGroupIdToGroup(clientInterfaceEntity.getInterfaceItem().getGroupId() != null
                                    ? clientInterfaceEntity.getInterfaceItem().getGroupId() : "others"))
                            .build();

                case STRING:
                    return StringItem.builder()
                            .id(clientInterfaceEntity.getInterfaceItem().getId())
                            .required(clientInterfaceEntity.getInterfaceItem().getIsRequired() != null && clientInterfaceEntity.getInterfaceItem().getIsRequired())
                            .value(StringUtils.isBlank(clientInterfaceEntity.getValue())
                                    ? StringUtils.EMPTY
                                    : clientInterfaceEntity.getValue())
                            .domainSpecificValue(clientInterfaceEntity.getDomainSpecificValue() == null ? null :
                                    StringUtils.isBlank(clientInterfaceEntity.getDomainSpecificValue())
                                    ? StringUtils.EMPTY
                                    : clientInterfaceEntity.getDomainSpecificValue())
                            .description(clientInterfaceEntity.getInterfaceItem()
                                    .getInterfaceDescriptions()
                                    .get(0).getDescription())
                            .isEncrypted(clientInterfaceEntity.getIsEncrypted() != null ? clientInterfaceEntity.getIsEncrypted() : false)
                            .group(convertGroupIdToGroup(clientInterfaceEntity.getInterfaceItem().getGroupId() != null
                                    ? clientInterfaceEntity.getInterfaceItem().getGroupId() : "others"))
                            .build();

                case LIST_STRING:
                    return ListStringItem.builder()
                            .id(clientInterfaceEntity.getInterfaceItem().getId())
                            .required(clientInterfaceEntity.getInterfaceItem().getIsRequired() != null && clientInterfaceEntity.getInterfaceItem().getIsRequired())
                            .value(StringUtils.isBlank(clientInterfaceEntity.getValue())
                                    ? mapper.readValue(clientInterfaceEntity.getInterfaceItem()
                                            .getDefaultValue(),
                                    new TypeReference<List<String>>() {
                                    })
                                    : mapper.readValue(clientInterfaceEntity.getValue(),
                                    new TypeReference<List<String>>() {
                                    }))
                            .domainSpecificValue(StringUtils.isBlank(clientInterfaceEntity.getDomainSpecificValue())
                                    ? null
                                    : mapper.readValue(clientInterfaceEntity.getDomainSpecificValue(),
                                    new TypeReference<List<String>>() {
                                    }))
                            .description(clientInterfaceEntity.getInterfaceItem()
                                    .getInterfaceDescriptions()
                                    .get(0).getDescription())
                            .isEncrypted(clientInterfaceEntity.getIsEncrypted() != null ? clientInterfaceEntity.getIsEncrypted() : false)
                            .group(convertGroupIdToGroup(clientInterfaceEntity.getInterfaceItem().getGroupId() != null
                                    ? clientInterfaceEntity.getInterfaceItem().getGroupId() : "others"))
                            .build();

                case LIST_INTEGER:
                    return ListIntegerItem.builder()
                            .id(clientInterfaceEntity.getInterfaceItem().getId())
                            .required(clientInterfaceEntity.getInterfaceItem().getIsRequired() != null && clientInterfaceEntity.getInterfaceItem().getIsRequired())
                            .value(StringUtils.isBlank(clientInterfaceEntity.getValue())
                                    ? mapper.readValue(clientInterfaceEntity.getInterfaceItem()
                                            .getDefaultValue(),
                                    new TypeReference<List<Integer>>() {
                                    })
                                    : mapper.readValue(clientInterfaceEntity.getValue(),
                                    new TypeReference<List<Integer>>() {
                                    }))
                            .domainSpecificValue(StringUtils.isBlank(clientInterfaceEntity.getDomainSpecificValue())
                                    ? null
                                    : mapper.readValue(clientInterfaceEntity.getDomainSpecificValue(),
                                    new TypeReference<List<Integer>>() {
                                    }))
                            .description(clientInterfaceEntity.getInterfaceItem()
                                    .getInterfaceDescriptions()
                                    .get(0).getDescription())
                            .isEncrypted(clientInterfaceEntity.getIsEncrypted() != null ? clientInterfaceEntity.getIsEncrypted() : false)
                            .group(convertGroupIdToGroup(clientInterfaceEntity.getInterfaceItem().getGroupId() != null
                                    ? clientInterfaceEntity.getInterfaceItem().getGroupId() : "others"))
                            .build();

                case SELECTOR:
                    Selector selector = StringUtils.isBlank(clientInterfaceEntity.getValue())
                            ? mapper.readValue(clientInterfaceEntity.getInterfaceItem()
                                    .getDefaultValue(),
                            Selector.class)
                            : mapper.readValue(clientInterfaceEntity.getValue(),
                            Selector.class);
                    Selector selector1 = StringUtils.isBlank(clientInterfaceEntity.getDomainSpecificValue())
                            ? mapper.readValue(clientInterfaceEntity.getInterfaceItem()
                                    .getDefaultValue(),
                            Selector.class)
                            : mapper.readValue(clientInterfaceEntity.getDomainSpecificValue(),
                            Selector.class);
                    return SelectorItem.builder()
                            .id(clientInterfaceEntity.getInterfaceItem().getId())
                            .required(clientInterfaceEntity.getInterfaceItem().getIsRequired() != null && clientInterfaceEntity.getInterfaceItem().getIsRequired())
                            .value(selector.getValue())
                            .domainSpecificValue(clientInterfaceEntity.getDomainSpecificValue() == null ? null :
                                    selector1.getValue())
                            .values(selector.getValues().stream().sorted().collect(Collectors.toList()))
                            .description(clientInterfaceEntity.getInterfaceItem()
                                    .getInterfaceDescriptions()
                                    .get(0).getDescription())
                            .isEncrypted(clientInterfaceEntity.getIsEncrypted() != null ? clientInterfaceEntity.getIsEncrypted() : false)
                            .group(convertGroupIdToGroup(clientInterfaceEntity.getInterfaceItem().getGroupId() != null
                                    ? clientInterfaceEntity.getInterfaceItem().getGroupId() : "others"))
                            .build();

                case LIST_PARAMETER:
                    return ListParameterItem.builder()
                            .id(clientInterfaceEntity.getInterfaceItem().getId())
                            .required(clientInterfaceEntity.getInterfaceItem().getIsRequired() != null && clientInterfaceEntity.getInterfaceItem().getIsRequired())
                            .value(StringUtils.isBlank(clientInterfaceEntity.getValue())
                                    ? mapper.readValue(clientInterfaceEntity.getInterfaceItem()
                                            .getDefaultValue(),
                                    ListParameter.class)
                                    : mapper.readValue(clientInterfaceEntity.getValue(),
                                    ListParameter.class))
                            .domainSpecificValue(StringUtils.isBlank(clientInterfaceEntity.getDomainSpecificValue())
                                    ? null
                                    : mapper.readValue(clientInterfaceEntity.getDomainSpecificValue(),
                                    ListParameter.class))
                            .description(clientInterfaceEntity.getInterfaceItem()
                                    .getInterfaceDescriptions()
                                    .get(0).getDescription())
                            .isEncrypted(clientInterfaceEntity.getIsEncrypted() != null ? clientInterfaceEntity.getIsEncrypted() : false)
                            .group(convertGroupIdToGroup(clientInterfaceEntity.getInterfaceItem().getGroupId() != null
                                    ? clientInterfaceEntity.getInterfaceItem().getGroupId() : "others"))
                            .build();

                case LIST_MANUFACTURER:
                    return ManufacturerItem.builder()
                            .id(clientInterfaceEntity.getInterfaceItem().getId())
                            .required(clientInterfaceEntity.getInterfaceItem().getIsRequired() != null && clientInterfaceEntity.getInterfaceItem().getIsRequired())
                            .value(StringUtils.isBlank(clientInterfaceEntity.getValue())
                                    ? mapper.readValue(clientInterfaceEntity.getInterfaceItem()
                                            .getDefaultValue(),
                                    new TypeReference<List<Manufacturer>>() {
                                    })
                                    : mapper.readValue(clientInterfaceEntity.getValue(),
                                    new TypeReference<List<Manufacturer>>() {
                                    }))
                            .domainSpecificValue(StringUtils.isBlank(clientInterfaceEntity.getDomainSpecificValue())
                                    ? null
                                    : mapper.readValue(clientInterfaceEntity.getDomainSpecificValue(),
                                    new TypeReference<List<Manufacturer>>() {
                                    }))
                            .description(clientInterfaceEntity.getInterfaceItem()
                                    .getInterfaceDescriptions()
                                    .get(0).getDescription())
                            .isEncrypted(clientInterfaceEntity.getIsEncrypted() != null ? clientInterfaceEntity.getIsEncrypted() : false)
                            .group(convertGroupIdToGroup(clientInterfaceEntity.getInterfaceItem().getGroupId() != null
                                    ? clientInterfaceEntity.getInterfaceItem().getGroupId() : "others"))
                            .build();

                default:
                    return null;
            }
        } catch (IOException e) {
            throw new FriendlyIllegalArgumentException(CAN_NOT_SERIALIZE_OBJECT, "interface item");
        }
    }

    public AbstractConfigProperty iotConfigEntityToConfigProperty(final IotConfigPropertyEntity propertyEntity,
                                                                  final Integer currentDomainId, final List<Integer> childDomainIds) {
        final String configType = propertyEntity.getValueType();
        final String value = propertyEntity.getValue();
        final Integer domainId = propertyEntity.getDomainId() == null || propertyEntity.getDomainId() < 1 ? 0 :
                propertyEntity.getDomainId();
        final boolean overridable = propertyEntity.getOverridable() != null && propertyEntity.getOverridable();
        final boolean writable = isWritable(currentDomainId, childDomainIds, overridable);

        switch (configType) {
            case "bool":
            case "boolean":
                final boolean boolValue;
                if (value.equals("1")) {
                    boolValue = true;
                } else if (value.equals("0")) {
                    boolValue = false;
                } else {
                    boolValue = Boolean.parseBoolean(value);
                }
                return BooleanProperty.builder()
                        .id(propertyEntity.getId())
                        .name(propertyEntity.getName())
                        .fullName(propertyEntity.getProgramName())
                        .description(propertyEntity.getDescription())
                        .domainId(domainId)
                        .overridable(overridable)
                        .writable(writable)
                        .value(boolValue)
                        .build();
            case "int":
            case "integer":
                return IntegerProperty.builder()
                        .id(propertyEntity.getId())
                        .name(propertyEntity.getName())
                        .fullName(propertyEntity.getProgramName())
                        .description(propertyEntity.getDescription())
                        .domainId(domainId)
                        .overridable(overridable)
                        .writable(writable)
                        .value(Integer.parseInt(value))
                        .minValue(propertyEntity.getMinValue())
                        .maxValue(propertyEntity.getMaxValue())
                        .build();
            case "select":
                return SelectorProperty.builder()
                        .id(propertyEntity.getId())
                        .name(propertyEntity.getName())
                        .fullName(propertyEntity.getProgramName())
                        .description(propertyEntity.getDescription())
                        .domainId(domainId)
                        .overridable(overridable)
                        .writable(writable)
                        .value(value)
                        .values(propertyEntity.getValidValues() == null
                                ? new ArrayList<>()
                                : Arrays.asList(propertyEntity.getValidValues().split(",")))
                        .build();

            default: // "string"
                return StringProperty.builder()
                        .id(propertyEntity.getId())
                        .name(propertyEntity.getName())
                        .fullName(propertyEntity.getProgramName())
                        .description(propertyEntity.getDescription())
                        .domainId(domainId)
                        .overridable(overridable)
                        .writable(writable)
                        .value(value)
                        .build();
        }
    }

  private static boolean isWritable(
      Integer currentDomainId, List<Integer> childDomainIds, boolean overridable) {
        if (currentDomainId == 0) {
          return true;
        } else return childDomainIds.contains(currentDomainId) && overridable;
    }

    public ClientInterfaceEntity toClientInterface(InterfaceSpecificDomain interfaceSpecificDomain,
                                                   AbstractConfigItem interfaceItem,
                                                   String value) {
        List<InterfaceDescriptionEntity> descriptions = new ArrayList<>();
        descriptions.add(InterfaceDescriptionEntity.builder()
                .description(interfaceItem.getDescription())
                .build());
        return ClientInterfaceEntity.builder()
                .domainSpecificValue(interfaceSpecificDomain.getValue())
                .value(value)
                .isEncrypted(interfaceItem.isEncrypted())
                .interfaceItem(InterfaceItemEntity.builder()
                        .isRequired(interfaceItem.isRequired())
                        .groupId(interfaceItem.getGroup())
                        .id(interfaceItem.getId())
                        .interfaceDescriptions(descriptions)
                        .valueType(interfaceItem.getValueType())
                        .build())
                .build();
    }

    public String groupNameToGroupId(String groupName) {
        switch (groupName) {
            case "Authentication":
                return  "auth";
            case "Diagnostics":
                return "diagnostics";
            case "ISP":
                return "isp";
            case "Generic timeouts":
                return "timeouts";
            case "Password policy":
                return "pass policy";
            default:
                return "others";
        }
    }

    public void setDomainSpecificValue(AbstractConfigItem c, String value) {
        try {
            switch (c.getValueType()) {
                case INTEGER:
                    boolean flag = StringUtils.isBlank(value) || value.equals("null");
                    ((IntegerItem) c).setDomainSpecificValue(flag ? null
                            : Integer.valueOf(value));
                    break;
                case BOOLEAN:
                    ((BooleanItem) c).setDomainSpecificValue(!StringUtils.isBlank(value) && Boolean.parseBoolean(value));
                    break;
                case STRING:
                    ((StringItem) c).setDomainSpecificValue(StringUtils.isBlank(value)
                            ? StringUtils.EMPTY
                            : value);
                    break;
                case LIST_STRING:
                    ((ListStringItem) c).setDomainSpecificValue(
                            mapper.readValue(value,
                            new TypeReference<List<String>>() {
                            }));
                    break;
                case LIST_INTEGER:
                    ((ListIntegerItem) c).setDomainSpecificValue(
                            mapper.readValue(value,
                                    new TypeReference<List<Integer>>() {
                                    }));
                    break;
                case SELECTOR:
                    Selector selector = mapper.readValue(value, Selector.class);
                    ((SelectorItem) c).setDomainSpecificValue(selector.getValue());
                    ((SelectorItem) c).setValues((selector.getValues().stream().sorted().collect(Collectors.toList())));
                    break;
                case LIST_PARAMETER:
                    ((ListParameterItem) c).setDomainSpecificValue(mapper.readValue(value, ListParameter.class));
                    break;
                case LIST_MANUFACTURER:
                    ((ManufacturerItem) c).setDomainSpecificValue(mapper.readValue(value,  new TypeReference<List<Manufacturer>>() {
                    } ));
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            throw new FriendlyIllegalArgumentException(CAN_NOT_SERIALIZE_OBJECT, "interface item");
        }
    }

    public static boolean isBase64Encoded(String value) {
        if (value == null || value.length() % 4 != 0 || value.equals("true")) {
            return false;
        }

        if (!value.matches("^[A-Za-z0-9+/]+={0,2}$")) {
            return false;
        }

        // Try to decode it
        try {
            Base64.getDecoder().decode(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
