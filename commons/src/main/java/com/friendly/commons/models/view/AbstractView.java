package com.friendly.commons.models.view;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Model that represents API version of View
 *
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
        property = AbstractView.TYPE_PROPERTY_NAME,
        defaultImpl = ViewType.class,
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "SearchView", value = SearchView.class),
        @JsonSubTypes.Type(name = "DeviceView", value = ListView.class),
        @JsonSubTypes.Type(name = "FrameView", value = FrameViewRequest.class),
        @JsonSubTypes.Type(name = "GroupUpdateView", value = GroupUpdateView.class),
})
public abstract class AbstractView implements Serializable {

    static final String TYPE_PROPERTY_NAME = "type";

    private Long id;
    private String name;
    private ViewType type;
    private boolean isDefaultUser;
    private boolean isDefaultPublic;

}
