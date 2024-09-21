package com.friendly.commons.models.settings.acs.whitelist;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = AbstractAcsWhiteList.VALUE_TYPE_PROPERTY_NAME,
        defaultImpl = WhiteListType.class,
        visible = true)
@JsonSubTypes({
        /* Names for subtype mapping should be gotten from {@link ValueType} enum */
        @JsonSubTypes.Type(name = "IP_RANGE", value = AcsWhiteListIp.class),
        @JsonSubTypes.Type(name = "SERIAL", value = AcsWhiteListSerial.class)
})
public abstract class AbstractAcsWhiteList implements Serializable {

    static final String VALUE_TYPE_PROPERTY_NAME = "type";

    private Integer id;
    private WhiteListType type;
    private String created;
    private String createdIso;
    private String creator;
    private Boolean onlyCreated;


}
